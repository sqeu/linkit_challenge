package com.linkit.spark.utils

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.ipc.RemoteException

class Utils {
  def putHDFS(sourcePath:String,destinationPath:String){
    val conf = new Configuration()
    try{
      val hdfs = FileSystem.get(new URI("hdfs://sandbox-hdp.hortonworks.com:8020"),conf)
      val srcPath = new Path(sourcePath)
      val destPath = new Path(destinationPath)
      hdfs.copyFromLocalFile(srcPath, destPath)
    }catch {
      case exception:RemoteException => println(exception.getMessage)
    }
  }
}
