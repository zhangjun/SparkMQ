package com.weibo.search.spark.MQ

import java.util.Date

private[MQ] class MQReceiverHandler(receiver: MQReceiver) extends Runnable  {
  def run(): Unit = {
    while(!receiver.isStopped()){
      val client = receiver.getClient
      
      try {
        var cnt = 0
        while(true){
          val getRes = client.get[String](receiver.getQueueName)
//      val getRes = "test data"
          if(getRes != null){
            cnt += 1
            var currTime = new Date()
            receiver.store(getRes + currTime.getTime + "-" + cnt)  
          } else {
            Thread.sleep(2000)
          }
        }
      } catch {
        case e:  Exception  =>
          //receiver.restart("Get Exception", e)
          println("Get Exception", e)
      }
      
    }
  }
}
  