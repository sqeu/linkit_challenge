package com.linkit.spark

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

class HbaseApp {
  def catalog = s"""{
                   |"table":{"namespace":"default", "name":"table1"},
                   |"rowkey":"key",
                   |"columns":{
                   |"col0":{"cf":"rowkey", "col":"key", "type":"string"},
                   |"col1":{"cf":"cf1", "col":"col1", "type":"boolean"},
                   |"col2":{"cf":"cf2", "col":"col2", "type":"double"},
                   |"col3":{"cf":"cf3", "col":"col3", "type":"float"},
                   |"col4":{"cf":"cf4", "col":"col4", "type":"int"},
                   |"col5":{"cf":"cf5", "col":"col5", "type":"bigint"},
                   |"col6":{"cf":"cf6", "col":"col6", "type":"smallint"},
                   |"col7":{"cf":"cf7", "col":"col7", "type":"string"},
                   |"col8":{"cf":"cf8", "col":"col8", "type":"tinyint"}
                   |}
                   |}""".stripMargin
  def createTable(){

  }
}
