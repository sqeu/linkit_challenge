package com.linkit.spark

package com.linkit.spark

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog
//import org.apache.hadoop.hbase.spark.datasources.HBaseTableCatalog
//import org.apache.spark.sql.datasources.hbase.HBaseTableCatalog
import org.apache.spark.sql.functions._

case class HBaseRecord(
                        col0: String,
                        col1: Boolean,
                        col2: Double,
                        col3: Float,
                        col4: Int,
                        col5: Long,
                        col6: Short,
                        col7: String,
                        col8: Byte)
object HBaseRecord
{
  def apply(i: Int, t: String): HBaseRecord = {
    val s = s"""row${"%03d".format(i)}"""
    HBaseRecord(s,
      i % 2 == 0,
      i.toDouble,
      i.toFloat,
      i,
      i.toLong,
      i.toShort,
      s"String$i: $t",
      i.toByte)
  }
}

class HbaseApp extends SparkSessionBuilder {
  def catalog = s"""{
                   |"table":{"namespace":"default", "name":"dangerous_driving"},
                   |"rowkey":"eventId",
                   ||"columns":{
                   ||"eventId":{"cf":"rowkey","col":"eventId","type":"string"},
                   ||"driverId":{"cf":"driver","col":"driverId","type":"string"},
                   ||"driverName":{"cf":"driver","col":"driverName","type":"string"},
                   ||"eventTime":{"cf":"event","col":"eventTime","type":"string"},
                   ||"eventType":{"cf":"event","col":"eventType","type":"string"},
                   ||"latitudeColumn":{"cf":"event","col":"latitudeColumn","type":"string"},
                   ||"longitudeColumn":{"cf":"event","col":"longitudeColumn","type":"string"},
                   ||"routeId":{"cf":"route","col":"routeId","type":"string"},
                   ||"routeName":{"cf":"route","col":"routeName","type":"string"},
                   ||"truckId":{"cf":"truck","col":"truckId","type":"string"}
                   ||}
                   ||}""".stripMargin
  val spark = buildSparkSession
  def withCatalog(cat: String): DataFrame = {
    spark
      .read
      .options(Map(HBaseTableCatalog.tableCatalog->cat))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()
  }
  def createTable(){
    //subir hdfs
    //leer csv
val filePath ="/workspace/data-hbase/dangerous-driver.csv"
    val dangerous_driver = spark.read.format("csv").option("header", "true").load(filePath)
    //cargar hdfs a tabla
    dangerous_driver.write.options(
      Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "4")).
      format("org.apache.spark.sql.execution.datasources.hbase").
      save()
val filePath2 ="/workspace/data-hbase/extra-driver.csv"
    val extra_driver = spark.read.format("csv").option("header", "true").load(filePath2)
    val extra_driver_mod = extra_driver.drop("eventId").
withColumn("eventId",lit("4"))	
    extra_driver_mod.write.options(
      Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "4")).
      format("org.apache.spark.sql.execution.datasources.hbase").
      save()

    val df = withCatalog(catalog)
    val df_mod = df.where("eventId==4").drop("routeName").withColumn("routeName",lit("Los Angeles to Santa Clara"))
	df_mod.write.options(Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "4")).
      format("org.apache.spark.sql.execution.datasources.hbase").
      save()

val df = withCatalog(catalog).filter(col("routeName").contains("Los Angeles")).collect()
    //probar q pasa si lo ejecuto otra vez?

  }

}
