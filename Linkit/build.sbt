name := "Spark1"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.6.0"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

libraryDependencies += "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0"

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.1" % "test" classifier "tests"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.2.1" % "test" classifier "tests"
libraryDependencies += "org.apache.spark" % "spark-hive_2.11" % "2.2.1"

libraryDependencies += "org.apache.hbase" % "hbase-client" % "2.0.2"
// https://mvnrepository.com/artifact/org.apache.hbase/hbase-common
libraryDependencies += "org.apache.hbase" % "hbase-common" % "2.0.2"
// https://mvnrepository.com/artifact/org.apache.hbase/hbase-server
libraryDependencies += "org.apache.hbase" % "hbase-server" % "2.0.2"
// https://mvnrepository.com/artifact/org.apache.hbase/hbase
libraryDependencies += "org.apache.hbase" % "hbase" % "2.0.2"

resolvers += "Hortonworks Repository" at "https://repo.hortonworks.com/content/repositories/releases/"
libraryDependencies += "org.apache.hbase" % "hbase-spark" % "2.0.2.3.1.0.6-1"
libraryDependencies += "com.hortonworks" % "shc-core" % "1.1.1-2.1-s_2.11"
libraryDependencies += "com.hortonworks" % "shc" % "1.1.1-2.1-s_2.11" pomOnly()
libraryDependencies += "com.hortonworks.hive" %% "hive-warehouse-connector" % "1.0.0.3.1.2.1-1"

// from "file:\\..\\lib"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
libraryDependencies += "org.apache.spark" %% "spark-tags" % "2.2.1" % "test" classifier "tests"

