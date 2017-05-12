package observatory

import scala.math.{sin, cos, acos, abs, pow, Pi}

import com.sksamuel.scrimage.{Image, Pixel}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    val powWithTemp = temperatures.map{case (l, t) => {
      val d = distance(l, location)
      if (d < 1) {
        return t
      } else {
        (1.0 / pow(d, 3), t)}
      }
    }
    val (a, b) = powWithTemp.foldLeft[(Double, Double)]((0.0, 0.0)){
      case ((s1, s2), (p, t)) => (s1 + p * t, s2 + p)
    }
    a / b
  }

  private def distance(p1: Location, p2: Location): Double = {
    acos(sin(p1.lat * Pi/180) * sin(p2.lat * Pi/180) + cos(p1.lat * Pi/180) * cos(p2.lat * Pi/180) * cos(abs(p1.lon * Pi/180 - p2.lon * Pi/180))) * 6371
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    var l = (Double.MinValue, Color(0,0,0))
    var u = (Double.MaxValue, Color(0,0,0))
    var minLow = Double.MinValue
    var minUpper = Double.MaxValue

    for (p <- points) {
      val diff = p._1 - value
      if (diff == 0) return p._2
      else if (diff < 0) {
        if (diff > minLow) {
          minLow = diff
          l = p
        }
      } else {
        if (diff < minUpper) {
          minUpper = diff
          u = p
        }
      }
    }

    if (l._1 == Double.MinValue) u._2
    else if (u._1 == Double.MaxValue) l._2
    else {
      lerp(l, u, value)
    }

  }

  private def lerp(l: (Double, Color), u: (Double, Color), value: Double): Color = {
    val x0 = (u._1 - value) / (u._1 - l._1)
    val x1 = (value - l._1) / (u._1 - l._1)
    Color(
      (l._2.red * x0   + u._2.red * x1 + 0.5).toInt,
      (l._2.green * x0 + u._2.green * x1 + 0.5).toInt,
      (l._2.blue * x0  + u._2.blue * x1 + 0.5).toInt
    )
  }


  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    val pixels: Seq[Pixel] = for (y <- 0 to 179;
         x <- 0 to 359
    ) yield {
      val l = Location(90 - y, x - 180)
      val t = predictTemperature(temperatures, l)
      val c = interpolateColor(colors, t)
      Pixel(c.red, c.green, c.blue, 100)
    }
    val image = Image(360, 180, pixels.toArray)
    image
  }

}

