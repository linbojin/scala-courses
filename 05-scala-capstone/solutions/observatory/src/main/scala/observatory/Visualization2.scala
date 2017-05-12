package observatory

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  /**
    * @param x X coordinate between 0 and 1
    * @param y Y coordinate between 0 and 1
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double
  ): Double = {
    d00 * (1 - x) * (1 - y) +
      d10 * x * (1 - y) +
      d01 * (1 - x) * y +
      d11 * x * y
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param zoom Zoom level of the tile to visualize
    * @param x X value of the tile to visualize
    * @param y Y value of the tile to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int
  ): Image = {
    val scale = 256
    val ps: Seq[(Int, Int)] = for (
      py <- 0 until scale;
      px <- 0 until scale
    ) yield (px, py)

    val pixels = ps.par.map{
      case (px, py) => {
        val l = Interaction.tileLocation(zoom + 8, x * scale + px, y * scale + py)
        val lat0 = if (l.lat >= 0) l.lat.toInt + 1 else l.lat.toInt
        val lon0 = if (l.lon >= 0) l.lon.toInt else l.lon.toInt - 1
        val lat1 = lat0 - 1
        val lon1 = lon0 + 1
        val t = bilinearInterpolation(
          l.lon - lon0,
          lat0 - l.lat,
          grid(lat0, lon0),
          grid(lat1, lon0),
          grid(lat0, lon1),
          grid(lat1, lon1)
        )
        val c = Visualization.interpolateColor(colors, t)
        Pixel(c.red, c.green, c.blue, 127)
      }
    }
    val image = Image(scale, scale, pixels.toArray)
    image
  }

}
