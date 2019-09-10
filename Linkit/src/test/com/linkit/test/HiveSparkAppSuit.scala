package com.linkit.test
import com.linkit.spark.apps.HiveSparkApp
import org.apache.spark.sql.Row
import org.apache.spark.sql.test.SharedSQLContext
import org.apache.spark.sql.types.{StringType, StructField, StructType}
class HiveSparkAppSuit extends SharedSQLContext{

   test("Data insertion HiveSparkApp") {
    /*val rdd = sqlContext.sparkContext.parallelize(Array(
      Row("100", "Joe Tan", "600234912", "130 Tambo Avenue", "Y", "miles"),
      Row("101", "Leo Dan", "234598591", "380 Cultura Rd.", "N", "miles"),
      Row("102", "San Holo", "589654212", "290 Sol Rd.", "Y", "miles")))

    val driverId = new StructField("driverId", StringType, nullable = true)
    val name = new StructField("name", StringType, nullable = true)
    val ssn = new StructField("ssn", StringType, nullable = true)
    val location = new StructField("location", StringType, nullable = true)
    val certified = new StructField("certified", StringType, nullable = true)
    val wagePlan = new StructField("wage_plan", StringType, nullable = true)

    val schema = StructType(Array(driverId, name, ssn, location, certified, wagePlan))
    //spark.sqlContext.createDataFrame(rdd, schema).createOrReplaceTempView("drivers")
    */
    val hiveSparkApp = new HiveSparkApp()
    hiveSparkApp.spark = spark
    val spark_data = "data-engineer-bootcamp-assessment-master/data-spark/"
    hiveSparkApp.insertData(spark_data+"drivers.csv","driver")
    val result = spark.sql("select * from driver").count()
    assert(result == 34)
  }

  test("Join HiveSparkApp") {
    val drivers = sqlContext.sparkContext.parallelize(Array(
      Row("100", "Joe Tan", "600234912", "130 Tambo Avenue", "Y", "miles"),
      Row("101", "Leo Dan", "234598591", "380 Cultura Rd.", "N", "miles"),
      Row("102", "San Holo", "589654212", "290 Sol Rd.", "Y", "miles")))

    val driverId_1 = new StructField("driverId", StringType, nullable = true)
    val name = new StructField("name", StringType, nullable = true)
    val ssn = new StructField("ssn", StringType, nullable = true)
    val location = new StructField("location", StringType, nullable = true)
    val certified = new StructField("certified", StringType, nullable = true)
    val wagePlan = new StructField("wage_plan", StringType, nullable = true)

    val schemaDriver = StructType(Array(driverId_1, name, ssn, location, certified, wagePlan))
    spark.sqlContext.createDataFrame(drivers, schemaDriver).createOrReplaceTempView("driver")

    val timesheet = sqlContext.sparkContext.parallelize(Array(
      Row("100", "1", "48", "3000"),
      Row("101", "1", "50", "3500"),
      Row("103", "2", "70", "4100")))

    val driverId_2 = new StructField("driverId", StringType, nullable = true)
    val week = new StructField("week", StringType, nullable = true)
    val hours_logged = new StructField("hours_logged", StringType, nullable = true)
    val miles_logged = new StructField("miles_logged", StringType, nullable = true)

    val schemaTimesheet = StructType(Array(driverId_2, week, hours_logged, miles_logged))
    spark.sqlContext.createDataFrame(timesheet, schemaTimesheet).createOrReplaceTempView("timesheet")

    val sparkJoin = new HiveSparkApp()
    sparkJoin.spark = spark
    val resultJoin = sparkJoin.join("driver","timesheet").count()
    assert(resultJoin == 2)
  }
}
