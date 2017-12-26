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

import sys
if sys.version >= "3":
    from io import BytesIO
else:
    from StringIO import StringIO
from py4j.protocol import Py4JJavaError

from pyspark.storagelevel import StorageLevel
from pyspark.serializers import PairDeserializer, NoOpSerializer, UTF8Deserializer, read_int
from pyspark.streaming import DStream

__all__ = ['MQUtils', 'utf8_decoder']


def utf8_decoder(s):
    """ Decode the unicode as UTF-8 """
    if s is None:
        return None
    return s.decode('utf-8')


class MQUtils(object):

    @staticmethod
    def createStream(ssc, hostname, port, queueName, threadNum,
                     storageLevel=StorageLevel.MEMORY_AND_DISK_2,
                     decoder=utf8_decoder):
        """
        Create an input stream that pulls events from Memcache.

        :param ssc:  StreamingContext object
        :param hostname:  Hostname of the slave machine to which the flume data will be sent
        :param port:  Port of the slave machine to which the flume data will be sent
        :param storageLevel:  Storage level to use for storing the received objects
        :return: A DStream object
        """
        jlevel = ssc._sc._getJavaStorageLevel(storageLevel)
        helper = MQUtils._get_helper(ssc._sc)
        jstream = helper.createStream(ssc._jssc, hostname, port, queueName, threadNum, jlevel)
        stream = DStream(jstream, ssc, NoOpSerializer())
        return stream.map(lambda v: decoder(v))
#        return DStream(jstream, ssc, UTF8Serializer())


    @staticmethod
    def _get_helper(sc):
        try:
            helperClass = sc._jvm.java.lang.Thread.currentThread().getContextClassLoader().loadClass("com.weibo.search.spark.MQ.MQUtilsPythonHandler")
            return helperClass.newInstance()
        except TypeError as e:
            if str(e) == "'JavaPackage' object is not callable":
                MQUtils._printErrorMsg(sc)
            raise

    @staticmethod
    def _printErrorMsg(sc):
        print("""
________________________________________________________________________________________________

  Spark Streaming's Flume libraries not found in class path. Try one of the following.

  1. Include the Flume library and its dependencies with in the
     spark-submit command as

     $ bin/spark-submit --packages org.apache.spark:spark-streaming-flume:%s ...

  2. Download the JAR of the artifact from Maven Central http://search.maven.org/,
     Group Id = org.apache.spark, Artifact Id = spark-streaming-flume-assembly, Version = %s.
     Then, include the jar in the spark-submit command as

     $ bin/spark-submit --jars <spark-streaming-flume-assembly.jar> ...

________________________________________________________________________________________________

""" % (sc.version, sc.version))
