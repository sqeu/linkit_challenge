package com.linkit.spark.utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.ipc.RemoteException

class Utils {
  def putHDFS(sourcePath:String,destinationPath:String){
    //http://blog.rajeevsharma.in/2009/06/using-hdfs-in-java-0200.html
    val conf = new Configuration()

    //conf.addResource(new Path("config/core-site.xml"));
    //conf.addResource(new Path("config/hdfs-site.xml"));
    //C:\Users\hugo_\Desktop\Work\Linkit\linkit_challenge\data-spark\drivers.csv
    val srcPath = new Path(sourcePath)
    val destPath = new Path("hdfs://sandbox-hdp.hortonworks.com:8020"+destinationPath)

    try{
      val hdfs = FileSystem.get(conf)
      hdfs.copyFromLocalFile(srcPath, destPath)
    }catch {
      case exception:RemoteException => println(exception.getMessage)
    }
  }
}
