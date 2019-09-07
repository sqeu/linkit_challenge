#My experience and further notes

Put here general information about the steps you took, what you liked, disliked, why did you do X instead of Y and so on.

## HDP Sandbox

I started using the Sandbox and I ...
vmware uses docker
bridge, to use other laptop
## HDFS & Hive on Spark

I did X because of Y...
<br>I had issues with Z...
<br>I liked doing T...
- upload the .csv files on data-spark to HDFS
    - Take into account that the app does the upload and no other external process
    - Spark won't do this, server might not get the path, unless shared path across cluster
-  Create table
    - Avro vs parquet
        - Parquet faster for reading
        - Column-oriented 
    - External table
        - No because of header also csv is slower to parse than parquet
    - Create table vs Create table as
        - More explicit when defining data types, constraints, more 
        control over the table.(https://stackoverflow.com/questions/6832181/creating-a-table-using-explicit-create-table-statement-versus-select-into)   
        - Create table as shorter code
        - Not pipeable unless that runs only once



    
Option 1: Create hive table that reads csv, read table
(header?)

create external table IF NOT EXISTS driver(
driverId,name,ssn,location,certified,wage-plan)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '/${hiveconf:PRM_HIVE_AMBIENTE}/bcp/edv/crmo/temp/fdg/FILE_CARTERA_TEMP/';

create external table 
timesheet(
driverId,week,hours-logged,miles-logged)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '/${hiveconf:PRM_HIVE_AMBIENTE}/bcp/edv/crmo/temp/fdg/FILE_CARTERA_TEMP/';

create external table truck_event_text_partition (
driverId,truckId,eventTime,eventType,longitude,latitude,eventKey,CorrelationId,driverName,routeId,routeName,eventDate)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
LOCATION '/${hiveconf:PRM_HIVE_AMBIENTE}/bcp/edv/crmo/temp/fdg/FILE_CARTERA_TEMP/';

Option 2: Spark puts csv to hdfs, reads, save it parquet, create external table, read table

partitions

## HBase

I did A because of B...
<br>I had issues with C...
<br>I liked doing D...

## Kafka
cuando se consulte a hbase
lo bote a topico
spark structured streaming escucha, procesa