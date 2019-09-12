package com.linkit.spark.apps

import com.hortonworks.hwc.HiveWarehouseSession
import com.linkit.spark.utils.{SparkSessionBuilder, Utils}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

class HiveCatalogApp {
  var spark: SparkSession = null
  @transient lazy val hive = HiveWarehouseSession.session(spark).build()

  def createTables() {
    hive.createDatabase("linkit",true)
    hive.executeUpdate(
      s"""create table if not exists linkit.driver (
                          |driverId string,name string,ssn string,location string,certified string,wage_plan string)
                          |using parquet""".stripMargin)
    hive.executeUpdate(
      s"""create table if not exists linkit.timesheet (
         |driverId string,week string,hours_logged string,miles_logged string)
         |using parquet""".stripMargin)
    hive.executeUpdate(
      s"""create table if not exists linkit.truck_event_text_partition (
         |driverId string,truckId string,eventTime string,eventType string,longitude string,latitude string,eventKey string
         |,CorrelationId string,driverName string,routeId string,routeName string,eventDate string)
         |using parquet""".stripMargin)
  }

  def insertData(filePath: String, tableName: String) {

    var df = spark.read.format("csv").option("header", "true").load(filePath)
    for (column <- df.columns) {
      if (column.contains("-")) {
        df = df.withColumnRenamed(column, column.replace("-", "_"))
      }
    }
    df.write.format("com.hortonworks.spark.sql.hive.llap.HiveWarehouseConnector").option("table", tableName).save()
  }

  def join(driverTable: String, timesheetTable: String): DataFrame = {
    val drivers = hive.executeQuery("select driverId, name from " + driverTable)
    val timesheet = hive.executeQuery("select driverId, hours_logged, miles_logged from " + timesheetTable)
    val agg_driver = drivers.join(timesheet, col("driver.driverId") === col("timesheet.driverId"))
    val df = agg_driver.select("driver.driverId", "driver.name", "timesheet.hours_logged", "timesheet.miles_logged")
    df
  }

}

object HiveCatalogApp extends SparkSessionBuilder with App{
  val app = new HiveCatalogApp()
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
  app.insertData(spark_data+"drivers.csv","linkit.driver")
  println("Inserting timesheet")
  app.insertData(spark_data+"timesheet.csv","linkit.timesheet")
  println("Inserting truck events")
  app.insertData(spark_data+"truck_event_text_partition.csv","linkit.truck_event_text_partition")
  println("Joining drivers and timesheet")
  app.join("linkit.driver","linkit.timesheet").show(false)

}