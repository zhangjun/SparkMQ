package com.weibo.search.spark.MQ

private[MQ] class MQReceiverHandler(receiver: MQReceiver) extends Runnable  {
  def run(): Unit = {
    while(!receiver.isStopped()){
      val client = receiver.getClient
      
      try {
        while(true){
          val getRes = client.get[String]("scalaTest")
//      val getRes = "test data"
          if(getRes != null){
            receiver.store(getRes)  
          } else {
            Thread.sleep(2000)
          }
        }
      } catch {
        case e:  Exception  =>
          //restart("Get Exception", e)
          println("Get Exception", e)
      }
      
    }
  }
}
  