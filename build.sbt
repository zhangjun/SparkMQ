name := "SparkMQ"
version := "0.1"
scalaVersion := "2.11.7"
//libraryDependencies := "org.apache.spark" % "spark-core_2.11"

libraryDependencies ++= Seq(
	// https://mvnrepository.com/artifact/io.monix/shade_2.11
	libraryDependencies += "io.monix" % "shade_2.11" % "1.10.0"
	// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming_2.11
	libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.1.0" % "provided"
	// https://mvnrepository.com/artifact/org.apache.spark/spark-core_2.11
	libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.1.0" % "provided"
	// https://mvnrepository.com/artifact/net.spy/spymemcached
	libraryDependencies += "net.spy" % "spymemcached" % "2.12.3"
)