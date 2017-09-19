package com.weibo.search.spark.MQ

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Seconds

object MainTest {
  var ssc: StreamingContext = null
  def main(args: Array[String]) {
    val sparkConf: SparkConf = new SparkConf().setAppName("MQTest")
    val sc = new SparkContext(sparkConf)
    val Array(server, port, queueName, numSecs) = args
    startMQStreaming(sc, server, port.toInt, queueName, numSecs.toInt)
  }
  
  def startMQStreaming(sc: SparkContext, server: String, port: Int, queueName: String, sec: Int = 3){
    val ssc = new StreamingContext(sc, Seconds(sec))
    //val MQParams = Map(
    //val MQStream = MQUtils.createStream(ssc, server, queueName, StorageLevel.MEMORY_AND_DISK_SER_2)
    val MQStream = new MQReceiver( server, port, queueName, StorageLevel.MEMORY_AND_DISK_SER_2)
    val lines = ssc.receiverStream(MQStream)
    val counts = lines.count()
    counts.print()
    ssc.start()
    ssc.awaitTermination()
  }
}