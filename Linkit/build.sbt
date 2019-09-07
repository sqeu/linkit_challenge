name := "Spark1"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.6.0"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.1"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.2.1"
libraryDependencies += "org.apache.spark" % "spark-hive_2.11" % "2.2.1"

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-spark
libraryDependencies += "org.apache.hbase" % "hbase-spark" % "2.0.2.3.1.0.6-1"
// https://mvnrepository.com/artifact/com.hortonworks/shc-core
libraryDependencies += "com.hortonworks" % "shc-core" % "1.1.1-2.1-s_2.11"
