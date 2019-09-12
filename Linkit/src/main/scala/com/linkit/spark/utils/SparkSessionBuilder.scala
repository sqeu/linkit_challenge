package com.linkit.spark.utils

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

class SparkSessionBuilder extends Serializable {
  
  def buildSparkSession: SparkSession = {

    @transient lazy val conf: SparkConf = new SparkConf()
      .setAppName("LinkitSpark")
      .set("spark.master", "local")
      .set("spark.hbase.host", "sandbox-hdp.hortonworks.com")
      .set("spark.hbase.port","2181" )

    @transient lazy val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()

    spark
  }
}