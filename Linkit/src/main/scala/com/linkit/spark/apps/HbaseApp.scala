package com.linkit.spark.apps

import com.linkit.spark.utils.{SparkSessionBuilder, Utils}
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

class HbaseApp  {
  var spark:SparkSession = null

  def withCatalog(cat: String): DataFrame = {
    spark
      .read
      .options(Map(HBaseTableCatalog.tableCatalog->cat))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()
  }

  def insert(catalog: String,df:DataFrame) {
    try {
      df
        .write.options(Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "4"))
        .format("org.apache.spark.sql.execution.datasources.hbase")
        .save()
    } catch {
      case argument:IllegalArgumentException => println("Updating spark may be required")
    }
  }

  def loadDangerousDriver(filePath:String,catalog:String) {
    val dangerous_driver = spark.read.format("csv").option("header", "true").load(filePath)
    insert(catalog, dangerous_driver)
  }

  def loadExtraDriver(filePath:String, catalog:String) {
    val extra_driver = spark.read.format("csv").option("header", "true").load(filePath)
      .drop("eventId")
      .withColumn("eventId", lit("4"))
    insert(catalog, extra_driver)
  }

  def updateRoute(id:String,newRoute:String,catalog:String){
    val df = withCatalog(catalog)
      .where("eventId=="+id)
      .drop("routeName")
      .withColumn("routeName",lit(newRoute))
    insert(catalog,df)
  }

  def getEventsWithRoute(origDest:String,catalog:String) {
    val df = withCatalog(catalog)
      .filter(col("routeName")
        .contains(origDest))
    df.show(false)
  }

}

object HbaseApp extends SparkSessionBuilder with App{
  val hbaseApp = new HbaseApp()
  hbaseApp.spark = buildSparkSession
  def catalog = s"""{
                   |"table":{"namespace":"default", "name":"dangerous_driving"},
                   |"rowkey":"eventId",
                   |"columns":{
                   |"eventId":{"cf":"rowkey","col":"eventId","type":"string"},
                   |"driverId":{"cf":"driver","col":"driverId","type":"string"},
                   |"driverName":{"cf":"driver","col":"driverName","type":"string"},
                   |"eventTime":{"cf":"event","col":"eventTime","type":"string"},
                   |"eventType":{"cf":"event","col":"eventType","type":"string"},
                   |"latitudeColumn":{"cf":"event","col":"latitudeColumn","type":"string"},
                   |"longitudeColumn":{"cf":"event","col":"longitudeColumn","type":"string"},
                   |"routeId":{"cf":"route","col":"routeId","type":"string"},
                   |"routeName":{"cf":"route","col":"routeName","type":"string"},
                   |"truckId":{"cf":"truck","col":"truckId","type":"string"}
                   |}
                   |}""".stripMargin

  val hdfs_data = "/workspace/"
  val hbase_data = hdfs_data+"data-hbase/"
  val sourcePath = "/workspace/data-hbase/"
  
  println("Putting files into HDFS")
  val utils = new Utils()
  utils.putHDFS(sourcePath,hdfs_data)
  
  println("Load dangerous driver")
  hbaseApp.loadDangerousDriver(hbase_data+"dangerous-driver.csv",catalog)
  println("Load extra driver")
  hbaseApp.loadExtraDriver(hbase_data+"extra-driver.csv",catalog)
  println("Updating route")
  hbaseApp.updateRoute("4","Los Angeles to Santa Clara",catalog)
  println("Getting event")
  hbaseApp.getEventsWithRoute("Los Angeles",catalog)

}
