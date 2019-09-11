#!/bin/bash
spark-submit --class com.linkit.spark.apps.HiveSparkApp /root/app/spark1_2.11-0.1.jar && spark-submit --class com.linkit.spark.apps.HbaseApp --repositories http://repo.hortonworks.com/content/groups/public/ --packages com.hortonworks:shc-core:1.1.1-2.1-s_2.11 /root/app/spark1_2.11-0.1.jar


