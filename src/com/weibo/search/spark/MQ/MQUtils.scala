package com.weibo.search.spark.MQ

import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.api.java.{JavaDStream, JavaReceiverInputDStream, JavaStreamingContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext

import scala.reflect.ClassTag

object MQUtils {
  def createStream(
      ssc: StreamingContext,
      server: String,
      port: Int,
      queueName: String,
      storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2
      ): ReceiverInputDStream[String] = {
    new MQInputDStream(ssc, server, port, queueName, storageLevel)
  }


  def createStream(
      jssc: JavaStreamingContext,
      server: String,
      port: Int,
      queueName: String
      ): JavaReceiverInputDStream[String] = {
    implicitly[ClassTag[AnyRef]].asInstanceOf[ClassTag[String]]
    createStream(jssc.ssc, server, port, queueName)
  }
  
   def createStream(
      jssc: JavaStreamingContext,
      server: String,
      port: Int, 
      queueName: String,
      storageLevel: StorageLevel
      ): JavaReceiverInputDStream[String] = {
    implicitly[ClassTag[AnyRef]].asInstanceOf[ClassTag[String]]
    createStream(jssc.ssc, server, port, queueName, storageLevel)
  }
   
}

private[MQ] class MQUtilsPythonHandler {
  def createStream(
      jssc: JavaStreamingContext,
      server: String,
      port: Int, 
      queueName: String,
      storageLevel: StorageLevel
      ): JavaDStream[String] = {
    MQUtils.createStream(jssc, server, port, queueName, storageLevel)
  }
}