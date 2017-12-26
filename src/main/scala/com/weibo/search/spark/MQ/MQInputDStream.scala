package com.weibo.search.spark.MQ

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.internal.Logging
import scala.reflect.ClassTag


import  java.net.{InetAddress, InetSocketAddress}
import java.util.concurrent.Executors
import com.google.common.util.concurrent.ThreadFactoryBuilder

import com.twitter.io.Buf
import com.twitter.util.{Await, Awaitable}
import com.twitter.conversions.time._

//import org.apache.log4j.Logger
//import org.apache.log4j.Level

import annotation.meta.field

// SpyMemcached
//import net.spy.memcached.{ConnectionFactoryBuilder, AddrUtil, MemcachedClient}
//import net.spy.memcached.transcoders.{Transcoder, SerializingTranscoder}
//import net.spy.memcached.compat.log.{Level, AbstractLogger}

// Xmemcached
import net.rubyeye.xmemcached.MemcachedClientBuilder
import net.rubyeye.xmemcached.MemcachedClient
import net.rubyeye.xmemcached.XMemcachedClientBuilder
import net.rubyeye.xmemcached.exception.MemcachedException
import net.rubyeye.xmemcached.utils.AddrUtil
import com.google.code.yanf4j.core.impl.StandardSocketOption;

// spotify folsom
import com.spotify.folsom.{MemcacheClient, MemcacheClientBuilder}
import com.spotify.folsom.AsciiMemcacheClient
import com.spotify.folsom.MemcacheClient
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.ListenableFuture

import java.util.concurrent.TimeUnit
import com.spotify.folsom.ConnectFuture

//private[streaming]
class MQInputDStream (
  @transient var ssc_ : StreamingContext,
  server: String,
  port: Int,
  queueName: String,
  threadNum: Int = 16,
  storageLevel: StorageLevel
) extends ReceiverInputDStream[String](ssc_) {
    override def getReceiver(): Receiver[String] = {
      new MQReceiver(server, port, queueName,  threadNum, storageLevel)
    }
    
}


class MQReceiver (
  server: String,
  port: Int, 
  queueName: String,
  threadNum: Int,
  storageLevel: StorageLevel
) extends Receiver[String] (storageLevel)  {
  
   def awaitResult[T](awaitable: Awaitable[T]): T = Await.result(awaitable, 5.seconds)
   
   // xmemcached 
   private var client : MemcachedClient = null
   // folsom
   private var client_folsom: AsciiMemcacheClient[String] = null
   lazy val receiverExecutor = Executors.newFixedThreadPool(threadNum, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("MQ Receiver Thread - %d").build())
  
  override def onStop(){
    receiverExecutor.shutdown()
    if(!receiverExecutor.awaitTermination(60, TimeUnit.SECONDS)){
      receiverExecutor.shutdownNow()
    }
    
    client_folsom.shutdown()
    ConnectFuture.disconnectFuture(client_folsom).get()
  }
  
  override def onStart(){
    printf("onStarting.....")
  
  //    // SpyMemcached   
//    lazy val client = {
//   //val addrs = new InetSocketAddress("10.73.12.142", 11233)
//   
//     val addrs = AddrUtil.getAddresses(server+ ":" + port)
//   
//     val cf = new ConnectionFactoryBuilder()
//                            .setProtocol(ConnectionFactoryBuilder.Protocol.TEXT)
//                            .build()
//                            
//     new MemcachedClient(cf, addrs) 
//   }
  
  
//    client = {   
//      val builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(server + ":" + port))
//      builder.setSocketOption(StandardSocketOption.SO_SNDBUF, 32*1024)
//      builder.setSocketOption(StandardSocketOption.TCP_NODELAY, false)
//      builder.getConfiguration().setSessionIdleTimeout(10000)
//      builder.setConnectionPoolSize(10)
//      builder.setConnectTimeout(3000L)
//   
//      //var client = builder.build()
//      builder.build()
//    }
//   client.setEnableHeartBeat(false)
//   client.setOpTimeout(3000L)
   
   client_folsom = MemcacheClientBuilder.newStringClient().withAddress(HostAndPort.fromParts(server, port)).withRetry(false).withConnections(10).connectAscii()
   try{
     ConnectFuture.connectFuture(client_folsom).get()
   } catch {
     case e: Exception =>
       client_folsom.shutdown()
       ConnectFuture.disconnectFuture(client_folsom).get()
       restart("Get Exception while connecting", e)
   }
   
   for( i <- 1 until threadNum){
     receiverExecutor.submit(new MQReceiverHandler(this))
   }
   
//    new Thread("MQ Receiver"){
//      override def run(){
//        //receiveMQ()
//        //process()
//        receiveHandler()
//        //processExample()
//      }
//    }.start()
 
  }
  
 private def processExample(){
    var cnt = 300
    while(cnt > 0){
      var str: String = "scala data for test"
      store(str)
      cnt = cnt - 1
    }  
  }
  
//private  def receiveHandler(){
//   println("receiving Data....")
//   
//   try {
//     while(true){
//      val getRes = client.get[String]("scalaTest")
////      val getRes = "test data"
//      if(getRes != null){
//        store(getRes)
////        val localAddr = InetAddress.getLocalHost.getHostAddress
////        store(getRes + localAddr)  
//      } else {
//        Thread.sleep(2000)
//      }
//      
//     }
////   
////      while(true){
////        println("Get Data....")
////        val future = client.asyncGet("scalaTest")
////        try {
////          val any = future.get(1, TimeUnit.SECONDS)
////          Option {
////            any match {
////              case x: java.lang.Byte => x.byteValue()
////              case x: java.lang.Short => x.shortValue()
////              case x: java.lang.Integer => x.intValue()
////              case x: java.lang.Long => x.longValue()
////              case x: java.lang.Float => x.floatValue()
////              case x: java.lang.Double => x.doubleValue()
////              case x: java.lang.Character => x.charValue()
////              case x: java.lang.Boolean => x.booleanValue()
////              case x => x
////            }
////          }
////        }
////        catch {
////          case e : net.spy.memcached.OperationTimeoutException => future.cancel(false)
////          None
////        }
//      
//    }catch{
//      case e:  Exception  =>
//        restart("Get Exception", e)
//    }
//    
//
//  }


  private[MQ] def getClient: MemcachedClient = {
    this.client
  }

  private[MQ] def getCF: AsciiMemcacheClient[String] = {
    this.client_folsom
  }
  private[MQ] def getQueueName: String = {
    this.queueName
  }
 private def toString(buf: Buf): String = {
    val Buf.Utf8(str) = buf
    str
  }
 
 def foo[U: Manifest](t: Any): U = if(implicitly[Manifest[U]] == manifest[Nothing])
   error("type not provided")
   else t.asInstanceOf[U]
   
}

//object StreamingExamples extends Logging {
//
//  /** Set reasonable logging levels for streaming if the user has not configured log4j. */
//  def setStreamingLogLevels() {
//    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
//    if (!log4jInitialized) {
//      // We first log something to initialize Spark's default logging, then we override the
//      // logging level.
//      logInfo("Setting log level to [WARN] for streaming example." +
//        " To override add a custom log4j.properties to the classpath.")
//      Logger.getRootLogger.setLevel(Level.WARN)
//    }
//  }
//}