#My experience and further notes

## HDP Sandbox

At first I wanted to use a virtual machine in VMWare with bridge connection because it would have been less resource intensive to deploy my app from another laptop, but it didn't work, HDP kept asking for a certain network. Therefore, I decided to keep the NAT connection. However, my laptop and IntelliJ were slowing down, I think it was because the Sandbox was causing my laptop to start swapping, 16GB of RAM on Windows were not enough. 
Also, the Sandbox kept killing HBase. Because of that reason, I tried to use docker but it failed due to size constraints.
I did not wanted to spend more time setting up the environment so I went back to use VMWare but this time I turned off as many services as I could and assigned more RAM to the VM. This worked and I was also able to keep HBase up, at the expense of not being able to keep IntelliJ open.


## HDFS & Hive on Spark
I wrote the code on this section and the following ones keeping in mind that I wanted to make it as reusable as posible.

- For uploading the csv files to HDFS:
    - Even thought Spark is able to do this, it can be inconsistent in a distributed system. Unless there is a shared folder with the same name across all the nodes, then it might be feasable. Additionally, I can reuse this portion of code for the next section of the challenge. Therefore, I used Hadoop libraries instead of Spark.
-  For the table creation:
    - I had to choose a format in which I could save my data, I asked myself which can be more convenient in this case. Avro is good if you want a flexible schema, however parquet can be faster at reading and is column-oriented making it great for analytical purposes. Additionally, in the following questions I need to create queries with certain columns, that was one of the reasons of why I chose parquet.
    - I also had the choice of creating the table as external and reading directly from the csv. However, the header in the csv would have make this a little cumbersome. Also, parsing the csv file is slower than parquet. I chose to create an external table with parquet because it was not only easier to setup the connection between Hive and Spark but faster overall.
    - Initially, I had the options to create the table explicitly (create table) or make spark/hive infer a schema (create table as). I wanted to be more explicit and be more in control when defining the table, because only then I can define data types, constraints and more for the table. Even though, "create table as" results in shorter code it might not be appropriate if you want it to run more than once. That is why I chose to do it via the "create table".
    - The schema I created has slightly different names than the headers in the csv, the main change is that instead of using "-" I used "_", I did this because Spark fails to parse the column names. It might think you are trying to do a math operation. I had two options, either escaping the columns with a backtick "`" and change my schema or change the name of the fields after reading it with Spark. I chose the latter because in the future it would be much easier to use than trying to figure out that you need to use backticks.
- Outputting the final dataframe was the most straightforward step, just an inner join. However, since this part of the challenge required me to read from Hive, I would have had problems connecting to Hive since I didn't have LLAP, more on this in next point.
- Spark connection with Hive
    - At first I had issues trying to see my results in Hive, I spent a quite some time trying to debug why my table was not showing in Hive and why the last dataframe was increasing in rows and duplicating them. This didn't happend to me before at clients. Then I found [this] (https://docs.cloudera.com/HDPDocuments/HDP3/HDP-3.1.0/integrating-hive/content/hive_hivewarehousesession_api_operations.html). So actually my table and data are not really in Hive, they are actually in the Spark catalog. 
    - With that in mind, I tried the Hive Warehouse Connector (HWC) so I could use Spark with the Hive catalog, however if I had used it entirely I wouldn't have been able to read the table because I didn't have LLAP (low-latency analytical processing) set-up. Therefore, having an external table was more convenient.
    - By the way, I had issues trying to connect to Hive through the HWC, I blame this on the documentation since it didn't specify that I needed to add a users to the jdbc string they tell you to use.
    - I ended up changing the metastore.catalog.default to hive from the hive-site.xml. Only then spark was able to read from the hive catalog easily.

## HBase

- Create table and data ingestion on HBase
    - First, I uploaded the data to HDFS using the same method as in the previous section, reusing the code.
    - I decided to use the Hortonworks Spark-HBase Connector (SHC) because of its suitability with dataframes and as stated in the [HortonWorks docs](https://docs.cloudera.com/HDPDocuments/HDP3/HDP-3.1.0/developing-spark-applications/content/selecting_a_connector.html) the schema is known at ingestion time. Plus it has some extra features that could be useful in the future (eg. support for Phoenix)
    - I defined the schema for the table that way using the principle of grouping the columns in families that are more likely to be used/retrieved together. 
    - At first I thought the library had a command to create the table together, but I couldn't find it. The hbase-client has it but I didn't use it. Instead, the table is created together with the data ingestion. Also, I did some testing to make sure that using the same method won't drop my table.
    - Before this, I have never used the option package when submitting a job. So, I had a few issues in figuring it out.
    - I also had issues trying to get Spark to connect to HBase. The library tells you to submit a file with your job, so it stays in the classpath, however that didn't work. After a quick look at the issues in the github page of the SHC, I figured that I needed to copy the HBase-site.xml to the Spark conf. 

- Adding a 4th element to the table from extra-driver.csv
    - If I add this only element as-is to the table created in the previous state, it will update the record with the eventId of 1. Assuming this is another unique event, I had to manually change the eventId to a consecutive eventId. I could have retrieved the biggest value in eventID then increment it, however it had to be done manually because in a real life scenario the rowkey doesn't update incrementally on every insert since this could create a bottleneck at writing time.
- Update id = 4 to display routeName as Los Angeles to Santa Clara instead of Santa Clara to San Diego
    - What I liked about this step is that I can write my query in Spark and the library translates it to its corresponding scan/get operation in HBase.
    - Since dataframes are inmutable, I had to create another dataframe by dropping a column and adding a new one with value changed.
- Outputs to console the Name of the driver, the type of event and the event Time if the origin or destination is Los Angeles.
    - I liked doing this step, I love handling the data! even thought it was pretty straightforward.


## Extras
- Writing unit tests
    - I liked this part because before this challenge I didn't know how to write unit tests for Spark and Scala. I saw [https://www.youtube.com/watch?v=4U9Me6shpno](Ted Malaska's conference) on this topic, it was pretty insightful.
    - While writing the tests I noticed that there had to be some minor changes in my code, so actually is really nice to learn that you have to have one more thing in mind when coding. 
    
- Containerized app
    - My first worry when I started making the dockerfile was how am I going to establish the connection between the docker and the HDP, after some time of failed attempts I remembered that the HDP Sandbox uses docker inside a VM and that when two containers are in the same user-defined network they have full connectivity between each other. Luckily, the sandbox uses a user-defined network.
    - I liked doing it this way, since I didn't have to worry about ports. Also, it was creative because I had to connect directly to the sandbox VM to take advantage of its docker.
    - The only file that I used from the HDP Sandbox was the hbase-site. After that, it all ran as it should. However, I had to handle an exception due to using an older Spark version.
    - I managed to get the application to work with the HDFS, HBase from the HDP Sandbox, however I couldn't manage to connect to the Hive metastore from the container, I suspect is because of a library or even the spark version.
    - I used as a base the dockerfile from p7hb/docker-spark, modified it by adding the necessary commands and files.

##Final comments

- I learned a lot from this challenge, especially about connectors and connections from another environment. I used to take for granted the connections with HDFS and Hive becuase when I go to clients they usually have everything ready to use. Also, they have older versions of their components, so it took me by surprise about that change on the default catalog that Spark uses.