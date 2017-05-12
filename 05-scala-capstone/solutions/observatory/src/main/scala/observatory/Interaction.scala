package observatory

import scala.math

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val n = math.pow(2.0, zoom)
    val lon_deg = x / n * 360.0 - 180.0
    val lat_deg = math.atan(math.sinh(math.Pi * (1 - 2 * y / n))) * 180 / math.Pi
    Location(lat_deg, lon_deg)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    val scale = 256
    val ps: Seq[(Int, Int)] = for (
      py <- 0 until scale;
      px <- 0 until scale
    ) yield (px, py)

    val pixels = ps.par.map{
      case (px, py) => {
        val l = tileLocation(zoom + 8, x * scale + px, y * scale + py)
        val t = Visualization.predictTemperature(temperatures, l)
        val c = Visualization.interpolateColor(colors, t)
        Pixel(c.red, c.green, c.blue, 127)
      }
    }

    val image = Image(scale, scale, pixels.toArray)
    image
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {
    yearlyData.foreach{
      case (year, records) => {
        for (zoom <- 0 to 3;
             x <- 0 until math.pow(2, zoom).toInt;
             y <- 0 until math.pow(2, zoom).toInt
        ) {
          generateImage(year, zoom, x, y, records)
        }
      }
    }
  }

}
