package observatory

import java.time.LocalDate
import java.sql.Date
import scala.collection.JavaConversions._

import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.{IntegerType, StructField, StructType, DoubleType}

/**
  * 1st milestone: data extraction
  */
object Extraction {
  SparkEnv.init("Test", "local[*]")

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    val sqlContext = SparkEnv.sql
    import sqlContext.implicits._
    val sNames = Seq("STN", "WBAN", "lat", "lon")
    val tNames = Seq("STN", "WBAN", "month", "day", "temp")
    val sPath = this.getClass.getResource(stationsFile).getPath()
    val tPath = this.getClass.getResource(temperaturesFile).getPath

    val sSchema = StructType(
      StructField("STN", IntegerType, true) ::
        StructField("WBAN", IntegerType, true) ::
        StructField("lat", DoubleType, true) ::
        StructField("lon", DoubleType, true) :: Nil
    )
    val tSchema = StructType(
      StructField("STN", IntegerType, true) ::
        StructField("WBAN", IntegerType, true) ::
        StructField("month", IntegerType, true) ::
        StructField("day", IntegerType, true) ::
        StructField("temp", DoubleType, true) :: Nil
    )

    val sDS = SparkEnv.sql.read.schema(sSchema).csv(sPath).toDF(sNames: _*).as[Station]
    val tDS = SparkEnv.sql.read.schema(tSchema).csv(tPath).toDF(tNames: _*).as[Temperature]

    val sDF = sDS.filter($"lat".isNotNull).filter($"lon".isNotNull).na.fill(0)
    val tDF = tDS.withColumn("temp", ($"temp" - 32) / 1.8).na.fill(0)

    val locUDF = udf((lat: Double, lon: Double) => {Location(lat, lon) })
    val dateUDF = udf((m: Int, d: Int) => { new Date(year - 1900, m-1, d) })

    val df = sDF.join(tDF, Seq("STN", "WBAN"), "inner")
      .withColumn("loc", locUDF($"lat", $"lon"))
      .withColumn("date", dateUDF($"month", $"day")).select("loc", "date", "temp")

    new Iterable[Record] {
      def iterator = asScalaIterator(df.as[Record].toLocalIterator)
    }.map(r => (r.date.toLocalDate, r.loc, r.temp))
  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    records.groupBy(_._2).map{case (k, l) => (k, l.map(_._3).sum / l.size)}
  }

}
