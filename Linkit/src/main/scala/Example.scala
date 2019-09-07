import com.linkit.spark.SparkSessionBuilder
import org.apache.hadoop.fs.Path
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions._


class SplitApp extends SparkSessionBuilder {
  val spark = buildSparkSession
  //First option for creating hive table through dataframe
  def createTables(){
    val df = spark.sql("select * from salary")
    df.createOrReplaceTempView("tempTable")
    spark.sql("")
    spark.sql("")
    spark.sql("")
  }
  def insertData(){
    //spark.sql("Create table yourtable as select * form tempTable")
    //data = hc.sql("select 1 as id, 10 as score")
    //data.write.mode("append").insertInto("my_table")
    //df.write.partitionBy('year', 'month').insertInto(...)
    //
    val driver = spark.read.csv("")
    val timesheet = spark.read.csv("")
    val truck_event = spark.read.csv("")

    //hiveContext.setConf("hive.exec.dynamic.partition", "true")
    //hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")

    //df.write().mode(SaveMode.Append).partitionBy("colname").saveAsTable("Table")
    driver.write.mode(SaveMode.Append).saveAsTable("driver")
    timesheet.write.mode(SaveMode.Append).saveAsTable("timesheet")
    truck_event.write.mode(SaveMode.Append).saveAsTable("truck_event_text_partition")
  }

  def join(): Unit ={
    val driver = spark.read.table("driver").select("driverId","name")
    val timesheet = spark.read.table("timesheet").select("driverId","hours-logged","miles_logged")
    //DRIVERID, NAME, HOURS_LOGGED, MILES_LOGGED
    val agg_driver = driver.join(timesheet,col("driverId"))
  }

}
object Example extends App {
  import org.apache.hadoop.conf.Configuration
  import org.apache.hadoop.fs.FileSystem

  val hadoopConf = new Configuration()

  val hdfs = FileSystem.get(hadoopConf)

  val srcPath = new Path("/home/edureka/Documents/data")
  val destPath = new Path("hdfs://xxx:8020/tranferrred_data")
  hdfs.copyFromLocalFile(srcPath, destPath)

}
