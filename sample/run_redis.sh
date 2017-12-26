#!/bin/bash

#local
#nohup /usr/local/spark/bin/spark-submit --master local[4] --py-files stuff.zip --jars SparkMQ-assembly-0.1.jar sparkmq_save.py 10.73.12.142 11993 scalaTest 8 2 > run.log 2>&1 &

# cluster
nohup /usr/local/spark/bin/spark-submit --master spark://10.77.2.21:7077 --py-files stuff.zip --jars SparkMQ-assembly-0.1.jar --total-executor-cores 128 --executor-memory 8g sparkmq_redis.py 10.73.12.198 11233 scalaTest 16 32 > run.log 2>&1 &

