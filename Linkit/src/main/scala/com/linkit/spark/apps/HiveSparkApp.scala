package com.linkit.spark.apps

import com.hortonworks.hwc.HiveWarehouseSession
import com.linkit.spark.utils.{SparkSessionBuilder, Utils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

class HiveSparkApp {
  var spark: SparkSession = null
  //@transient lazy val hive = HiveWarehouseSession.session(spark).build()
  def createTables() {
   spark.sql("create database if not exists linkit")
    //hive.createDatabase("linkit",true)
spark.sql(
      s"""create external table if not exists linkit.driver (
         |driverId string,name string,ssn string,location string,certified string,wage_plan string)
         |stored as parquet
         |location '/data/driver'""".stripMargin)

    spark.sql(
      s"""create external table if not exists linkit.timesheet (
         |driverId string,week string,hours_logged string,miles_logged string)
         |stored as parquet
         |location '/data/timesheet'""".stripMargin)
    spark.sql(
      s"""create table if not exists linkit.truck_event_text_partition (
         |driverId string,truckId string,eventTime string,eventType string,longitude string,latitude string,eventKey string
         |,CorrelationId string,driverName string,routeId string,routeName string,eventDate string)
         |stored as parquet
         |location '/data/truck_event_text_partition'""".stripMargin)
  }

  def insertData(filePath: String, tableName: String) {

    var df = spark.read.format("csv").option("header", "true").load(filePath)
    for (column <- df.columns) {
      if (column.contains("-")) {
        df = df.withColumnRenamed(column, column.replace("-", "_"))
      }
    }
    df.write.format("parquet").mode(SaveMode.Append)
      .save("/data/"+tableName)
    //hive.execute("msck repair table linkit."+tableName)
  }

  def join(driverTable: String, timesheetTable: String): DataFrame = {
    val drivers = spark.read.table(driverTable).select("driverId", "name")
    val timesheet = spark.read.table(timesheetTable).select("driverId", "hours_logged", "miles_logged")
    val agg_driver = drivers.join(timesheet, col("driver.driverId") === col("timesheet.driverId"))
    val df = agg_driver.select("driver.driverId", "driver.name", "timesheet.hours_logged", "timesheet.miles_logged")
    df
  }
}
object HiveSparkApp extends SparkSessionBuilder with App{
  val app = new HiveSparkApp()
  app.spark=buildSparkSession
  val hdfs_data = "/workspace/"
  val spark_data = hdfs_data+"data-spark/"
  val sourcePath = "/workspace/data-spark/"

  println("Putting files into HDFS")
  val utils = new Utils()
  utils.putHDFS(sourcePath,hdfs_data)

  println("Creating tables")
  app.createTables()
  println("Inserting timesheet")

	app.insertData(sourcePath +"timesheet.csv","timesheet")
hive.executeQuery("select driverId, name from linkit.driver")
spark.sql("select driverId, name from linkit.driver").show()
hive.execute("msck repair table linkit.driver")
  app.insertData(spark_data+"drivers.csv","linkit.driver")
  println("Inserting timesheet")
  app.insertData(spark_data+"timesheet.csv","linkit.timesheet")
  println("Inserting truck events")
  app.insertData(spark_data+"truck_event_text_partition.csv","linkit.truck_event_text_partition")
  println("Joining drivers and timesheet")
  app.join("linkit.driver","linkit.timesheet").show(false)


}
