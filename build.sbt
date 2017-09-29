name := "SparkMQ"
version := "0.1"
scalaVersion := "2.11.8"
//libraryDependencies := "org.apache.spark" % "spark-core_2.11"

// https://mvnrepository.com/artifact/com.twitter/finagle-core_2.11
//libraryDependencies += "com.twitter" % "finagle-core_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.twitter/finagle-init_2.11
//libraryDependencies += "com.twitter" % "finagle-init_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.twitter/finagle-netty4_2.11
//libraryDependencies += "com.twitter" % "finagle-netty4_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.twitter/finagle-memcached_2.11
//libraryDependencies += "com.twitter" % "finagle-memcached_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.twitter/finagle-stats_2.11
//libraryDependencies += "com.twitter" % "finagle-stats_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.twitter/finagle-test_2.11
//libraryDependencies += "com.twitter" % "finagle-test_2.11" % "6.31.0" % "test"
// https://mvnrepository.com/artifact/com.twitter/finagle-toggle_2.11
//libraryDependencies += "com.twitter" % "finagle-toggle_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "14.0.1" % "provided"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-auth
libraryDependencies += "org.apache.hadoop" % "hadoop-auth" % "2.7.3" % "provided"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.3" % "provided"
// https://mvnrepository.com/artifact/com.twitter/util-core_2.11
libraryDependencies += "com.twitter" % "util-core_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/com.twitter/util-tunable_2.11
//libraryDependencies += "com.twitter" % "util-tunable_2.11" % "7.1.0"
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming_2.11
libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.1.0" % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.11
libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.1.0" % "provided"
// https://mvnrepository.com/artifact/com.googlecode.xmemcached/xmemcached
//libraryDependencies += "com.googlecode.xmemcached" % "xmemcached" % "2.3.2"


libraryDependencies ++= Seq(
	// https://mvnrepository.com/artifact/com.twitter/finagle-core_2.11
	//libraryDependencies += "com.twitter" % "finagle-core_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.twitter/finagle-init_2.11
	//libraryDependencies += "com.twitter" % "finagle-init_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.twitter/finagle-netty4_2.11
	//libraryDependencies += "com.twitter" % "finagle-netty4_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.twitter/finagle-memcached_2.11
	//libraryDependencies += "com.twitter" % "finagle-memcached_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.twitter/finagle-stats_2.11
	//libraryDependencies += "com.twitter" % "finagle-stats_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.twitter/finagle-test_2.11
	//libraryDependencies += "com.twitter" % "finagle-test_2.11" % "6.31.0" % "test"
	// https://mvnrepository.com/artifact/com.twitter/finagle-toggle_2.11
	//libraryDependencies += "com.twitter" % "finagle-toggle_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.google.guava/guava
	//libraryDependencies += "com.google.guava" % "guava" % "14.0.1" % "provided"
	// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-auth
	//libraryDependencies += "org.apache.hadoop" % "hadoop-auth" % "2.7.3" % "provided"
	// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
	//libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.7.3" % "provided"
	// https://mvnrepository.com/artifact/com.twitter/util-core_2.11
	//libraryDependencies += "com.twitter" % "util-core_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/com.twitter/util-tunable_2.11
	//libraryDependencies += "com.twitter" % "util-tunable_2.11" % "7.1.0"
	// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming_2.11
	//libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.1.0" % "provided"
	// https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.11
	//libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.1.0" % "provided"
)

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case "netty-3.10.1" => MergeStrategy.discard
  case PathList("META-INF", "io.netty.versions.properties", xs @ _*) => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}