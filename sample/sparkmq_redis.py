#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""
 Counts words in UTF8 encoded, '\n' delimited text received from the network every second.
 Usage: SparkMQ_WordCount.py <hostname> <port> <queuename>

 To run this on your local machine, you need to setup Flume first, see
 https://flume.apache.org/documentation.html

 and then run the example
    `$ bin/spark-submit 
      --master spark://10.77.2.21:7077
	  --jars \
      SparkMQ-assembly-0.1.jar \
      SparkMQ_WordCount.py \
      10.77.2.1 11993 scalaTest
"""
from __future__ import print_function

import sys
import time

import redis
from redis.client import Redis
import ConnectionPool

from pyspark import SparkContext, SparkConf
from pyspark.streaming import StreamingContext
from pyspark.storagelevel import StorageLevel
from memcache import MQUtils

__author__ = 'Jun Zhang'

def is_valid_date(str):
	try:
		if ":" in str:
			time.strptime(str, "%Y-%m-%d %H:%M:%S")
		else:
			time.strptime(str, "%Y-%m-%d")

		return True
	except:
		return False

def sendPartition(iter):
	#pool = redis.BlockingConnectionPool(host = '10.77.2.21', port = 20170)
    r = redis.Redis(connection_pool = ConnectionPool.getConnection())
	#pipe = r.pipeline(transactio = true)
	#pipe command
	#pipe.execute()
    for record in iter:
        uid = record[0]
        cnt = int(record[1])
        r.incr(uid, cnt)

def sendPartition0(iter):
	#pool = redis.BlockingConnectionPool(host = '10.77.2.21', port = 20170)
    r = redis.Redis(connection_pool = ConnectionPool.getConnection())
	#pipe = r.pipeline(transactio = true)
	#pipe command
	#pipe.execute()
    for record in iter:
        r.incr('pv', 1)

def sendPartition2(iter):
	#pool = redis.BlockingConnectionPool(host = '10.77.2.21', port = 20170)
    r = redis.Redis(connection_pool = ConnectionPool.getConnection())
	#pipe = r.pipeline(transactio = true)
	#pipe command
	#pipe.execute()
    for record in iter:
        key = record[0]
        cnt = int(record[1])
        time, query  = (key[0], key[1])		
        r.hincrby(time, query, cnt)
        r.expire(time, 24*3600) 


if __name__ == "__main__":
    if len(sys.argv) != 6:
        print("Usage: SparkMQ_WordCount.py <server_ip> <port> <queue_name> <thread_num> <receiver_num>", file=sys.stderr)
        exit(-1)

    #sc = SparkContext("local[2]","PythonStreamingSparkMQWordCount")
	#conf = SparkConf().setAppName("PythonStreamingSparkMQWordCount").setMaster('spark://' + '10.77.1.12:7077')
	#sc = SparkContext(conf = conf)
    #sc = SparkContext(appName="PythonStreamingSparkMQWordCount", master="local[4]")
    sc = SparkContext(appName="PythonStreamingSparkMQWordCount")

    ssc = StreamingContext(sc, 60)

    server, port, queuename, threadnum, numstream = sys.argv[1:]
    mqDstreams = [MQUtils.createStream(ssc, server, int(port), queuename, int(threadnum), StorageLevel.MEMORY_AND_DISK_SER) for _ in range (int(numstream))]
    mqRDD = ssc.union(*mqDstreams)
    #kvs = MQUtils.createStream(ssc, server, int(port), queuename, StorageLevel.MEMORY_ONLY)
    mqData = mqRDD.flatMap(lambda x: x.split("\n"))
    lines = mqData.map(lambda x: x.split('\t')).filter(lambda x: len(x) >= 7 and is_valid_date(x[0]))
    counts = lines.map(lambda x: ((x[0][:16], x[1]), 1)).reduceByKey(lambda a, b: a+b)                                                                    
	#nv_cnts = lines.foreachRDD(lambda line: (line[2], 1))
    #uv_cnts = lines.map(lambda x: (x[2], 1)).reduceByKey(lambda a, b: a+b)                                                                    
	
    #uv_cnts.cache()
    #lines.cache()

	# redis 
    #lines.foreachRDD(lambda rdd: rdd.foreachPartition(sendPartition0))
    counts.foreachRDD(lambda rdd: rdd.foreachPartition(sendPartition2))
    #uv_cnts.foreachRDD(lambda rdd: rdd.foreachPartition(sendPartition))
#    counts = lines.flatMap(lambda line: line.split(" ")) \
#        .map(lambda word: (word, 1)) \
#        .reduceByKey(lambda a, b: a+b)
#    counts.pprint()
    #counts.saveAsTextFiles("hdfs://10.77.1.12:9000/tmp/zhangjun9/spark/result")
    #uv_cnts.saveAsTextFiles("hdfs://10.77.1.12:9000/tmp/zhangjun9/spark/uv")

    ssc.start()
    ssc.awaitTermination()
