package com.linkit.spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class SparkSessionBuilder extends Serializable {
  // Build a spark session. Class is made serializable so to get access to SparkSession in a driver and executors.
  // Note here the usage of @transient lazy val

  def buildSparkSession: SparkSession = {

    @transient lazy val conf: SparkConf = new SparkConf()
      .setAppName("Structured Streaming from Kafka to Cassandra")
      .set("spark.master", "local")
      //.set("spark.cassandra.connection.host","192.168.99.100")
      //.set("spark.sql.streaming.checkpointLocation", "checkpoint")
      .set("spark.cassandra.connection.host", "10.79.6.88,10.79.6.90,10.79.6.91,10.79.6.99,10.79.6.101")
      .set("spark.cassandra.connection.port", "9042")
      .set("spark.cassandra.output.ignoreNulls", "true")
      //.set("spark.cassandra.connection.compression","SNAPPY")
      .set("spark.cassandra.auth.username", "APSHDES")
      .set("spark.cassandra.auth.password", "Q56ieTYpo90")
      .set("spark.cassandra.connection.ssl.enabled","true")
      .set("spark.cassandra.connection.ssl.keyStore.password","6ZVRrdXD25HEppm3eTSl")
      .set("spark.cassandra.connection.ssl.keyStore.path","/opt/security/Alexandria-keystore-cloud-des.jks")
      .set("spark.cassandra.connection.ssl.trustStore.path","/opt/security/Alexandria-truststore-cloud-des.jks")
      .set("spark.cassandra.connection.ssl.trustStore.password","6ZVRrdXD25HEppm3eTSl")


    //@transient lazy val app = new SparkBaseApplication(Array(classOf[com.linkit.spark.SparkSessionBuilder]))

    @transient lazy val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()

    spark
  }
}