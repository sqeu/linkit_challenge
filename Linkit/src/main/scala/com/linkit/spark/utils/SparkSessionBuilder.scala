package com.linkit.spark.utils

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class SparkSessionBuilder extends Serializable {
  // Build a spark session. Class is made serializable so to get access to SparkSession in a driver and executors.
  // Note here the usage of @transient lazy val

  def buildSparkSession: SparkSession = {

    @transient lazy val conf: SparkConf = new SparkConf()
      .setAppName("LinkitSpark")
      .set("spark.master", "local")
      .set("spark.hbase.host", "sandbox-hdp.hortonworks.com")
      .set("spark.hbase.port","2181" )

    //@transient lazy val app = new SparkBaseApplication(Array(classOf[com.linkit.spark.utils.SparkSessionBuilder]))

    @transient lazy val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()

    spark
  }
}