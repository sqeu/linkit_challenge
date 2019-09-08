import org.apache.hadoop.ipc.RemoteException
import com.linkit.spark.SparkSessionBuilder
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

class HiveSpark extends SparkSessionBuilder {
  val spark = buildSparkSession
  val hdfs_data = "/workspace/"
  val spark_data = hdfs_data+"data-spark/"
  val sourcePath = "/workspace/data-spark/"
  def putHDFS(sourcePath:String){
  //http://blog.rajeevsharma.in/2009/06/using-hdfs-in-java-0200.html
    val conf = new Configuration()

    //conf.addResource(new Path("config/core-site.xml"));
    //conf.addResource(new Path("config/hdfs-site.xml"));
    //C:\Users\hugo_\Desktop\Work\Linkit\linkit_challenge\data-spark\drivers.csv
    val srcPath = new Path(sourcePath)
    val destPath = new Path("hdfs://sandbox-hdp.hortonworks.com:8020"+hdfs_data)

    try{
      val hdfs = FileSystem.get(conf)
      hdfs.copyFromLocalFile(srcPath, destPath)
    }catch {
      case exception:RemoteException => println(exception.getMessage)
    }
  }
  //First option for creating hive table through dataframe
  def createTables(){
    spark.sql("CREATE DATABASE IF NOT EXISTS linkit")

    spark.sql(
      s"""create table if not exists linkit.driver (
         |driverId string,name string,ssn string,location string,certified string,wage_plan string)
         |using parquet""".stripMargin)
    spark.sql(
      s"""create table if not exists linkit.timesheet (
         |driverId string,week string,hours_logged string,miles_logged string)
         |using parquet""".stripMargin)
    spark.sql(
      s"""create table if not exists linkit.truck_event_text_partition (
         |driverId string,truckId string,eventTime string,eventType string,longitude string,latitude string,eventKey string
         |,CorrelationId string,driverName string,routeId string,routeName string,eventDate string)
         |using parquet""".stripMargin)
  }

  def insertData(filePath:String,tableName:String){

    var df = spark.read.format("csv").option("header", "true").load(filePath)
    for (column <- df.columns){
      if (column.contains("-")){
        df = df.withColumnRenamed(column,column.replace("-","_"))
      }
    }

    df.write.format("parquet").mode(SaveMode.Append).saveAsTable(tableName)
      //.select("driverId","name","ssn" ,"location" ,"certified",col("wage-plan").as("wage_plan"))
    /*var timesheet = spark.read.format("csv").option("header", "true").load(spark_data+"timesheet.csv")
    for (column <- timesheet.columns){
      if (column.contains("-")){
        timesheet = timesheet.withColumnRenamed(column,column.replace("-","_"))
      }
    }
    val truck_event = spark.read.format("csv").option("header", "true").load(spark_data+"truck_event_text_partition.csv")

    //hiveContext.setConf("hive.exec.dynamic.partition", "true")
    //hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")

    //df.write().mode(SaveMode.Append).partitionBy("colname").saveAsTable("Table")
    drivers.write.format("parquet").mode(SaveMode.Append).saveAsTable("driver")
    timesheet.write.format("parquet").mode(SaveMode.Append).saveAsTable("timesheet")
    truck_event.write.format("parquet").mode(SaveMode.Append).saveAsTable("truck_event_text_partition")*/
  }

  def join():DataFrame={
    val drivers = spark.read.table("linkit.driver").select("driverId","name")
    val timesheet = spark.read.table("linkit.timesheet").select("driverId","hours_logged","miles_logged")
    //DRIVERID, NAME, HOURS_LOGGED, MILES_LOGGED
    val agg_driver = drivers.join(timesheet,col("driver.driverId")===col("timesheet.driverId"))
    agg_driver
  }

}
object HiveSpark extends App {
  val example = new HiveSpark()
  example.createTables()
  example.insertData(example.spark_data+"drivers.csv","linkit.driver")
  example.insertData(example.spark_data+"timesheet.csv","linkit.timesheet")
  example.insertData(example.spark_data+"truck_event_text_partition.csv","linkit.truck_event_text_partition")
  example.join().select("driver.driverId","driver.name",  "timesheet.hours_logged", "timesheet.miles_logged").write.csv("/tmp/shakespeareWordCount")
}
