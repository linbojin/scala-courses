package observatory

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}

/**
  * Created by @linbojin on 29/4/17.
  */
object SparkEnv {

  var initialized: Boolean = false

  var spark: SparkContext = _
  var sql: SparkSession = _

  def init(appName: String, master: String = ""): Unit = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    if (!initialized) {
      val conf = new SparkConf().setAppName(appName)
      if (!master.isEmpty) {
        conf.setMaster(master)
      }

      sql = SparkSession
        .builder()
        .config(conf)
        .getOrCreate()
      spark = sql.sparkContext

      initialized = true
    }
  }

}
