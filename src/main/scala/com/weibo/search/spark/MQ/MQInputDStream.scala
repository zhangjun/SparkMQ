package com.weibo.search.spark.MQ

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver.Receiver
import org.apache.spark.internal.Logging

import com.twitter.finagle.Name
import com.twitter.finagle.memcached.Client
import  java.net.{InetAddress, InetSocketAddress}
import com.twitter.finagle.Address
import com.twitter.finagle.Memcached
import com.twitter.io.Buf
import com.twitter.util.{Await, Awaitable}
import com.twitter.conversions.time._


//private[streaming]
class MQInputDStream (
  _ssc : StreamingContext,
  server: String,
  port: Int,
  queueName: String,
  storageLevel: StorageLevel
) extends ReceiverInputDStream[String](_ssc) {
    def getReceiver(): Receiver[String] = {
      new MQReceiver(server, port, queueName,  storageLevel)
    }
}


class MQReceiver (
  server: String,
  port: Int, 
  queueName: String,
  storageLevel: StorageLevel
) extends Receiver[String] (storageLevel) {
  
   def awaitResult[T](awaitable: Awaitable[T]): T = Await.result(awaitable, 5.seconds)
  
  def onStop(){
    
  }
  
  def onStart(){
    
    new Thread(){
      override def run(){
        receiveMQ()
      }
    }.start()
 
    
    
//    val addr = Address(new InetSocketAddress("10.73.12.142", 11993))
//    val dest = Name.bound(addr)
//    
//    val service = Memcached.client
//          .withEjectFailedHost(true)
//          .withTransport.connectTimeout(500.milliseconds)
//          .withRequestTimeout(3.seconds)
//          .connectionsPerEndpoint(4)
//          .newService(dest, "client_name")      
//    val client = Client(service)
//    
//    try {
//      
//    //val service = Memcached.client.connectionsPerEndpoint(1).newService(dest, "client_name")
//    //val client = Client(service)
//      
//      while(true){
//        val res = awaitResult(client.get("scalaTest")).get  
//        //println(toString(res))
//      }
//      
//    }catch{
//      case e: shade.TimeoutException =>
//        restart("Error! while connection", e)
//    }
  }
  
  def receiveMQ(){
     
    val addr = Address(new InetSocketAddress(server, port))
    val dest = Name.bound(addr)
    
    val service = Memcached.client
          .withEjectFailedHost(true)
          .withTransport.connectTimeout(500.milliseconds)
          .withRequestTimeout(3.seconds)
          .connectionsPerEndpoint(4)
          .newService(dest, "client_name")      
    val client = Client(service)
    
    try {
   
      while(true){
        println("Get Data....")
        val getRes = Await.result(client.get("scalaTest")) 
        getRes match {
          case Some(Buf.Utf8(mqData)) => store(mqData)
          case None => Thread.sleep(200)
        }
        //println(toString(res))
      }
      
    }catch{
      case e:  com.twitter.util.TimeoutException =>
        restart("Get Exception", e)
    }
  }
  
  def toString(buf: Buf): String = {
    val Buf.Utf8(str) = buf
    str
  }
   
}