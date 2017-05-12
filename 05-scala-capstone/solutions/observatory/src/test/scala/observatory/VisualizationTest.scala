package observatory

import scala.math.abs

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {

  test("predictTemperature") {
    val t = Iterable((Location(1.0, 3.0), 10.0),
    (Location(1.0, 2.0), 20.0))
    assert(Visualization.predictTemperature(t, Location(1.0, 2.5)) == 15)
    assert(Visualization.predictTemperature(t, Location(1.0, 3.000001)) == 10.0)
    val t1 = Visualization.predictTemperature(t, Location(1.0, 2.6))
    assert( abs(10.0 - t1) < abs(t1 - 20.0))
    val t2 = Visualization.predictTemperature(t, Location(1.0, 2.4))
    assert( abs(10.0 - t2) > abs(t2 - 20.0))
  }

  test("predictTemperature failed test") {
    val t = Iterable((Location(45.0,-90.0), -5.292945581250422), (Location(-45.0,0.0),1.0))
    val t1 = Visualization.predictTemperature(t, Location(88.0,-176.0))
    assert( abs(-5.292945581250422 - t1) < abs(t1 - 1.0))
  }

  test("interpolateColor") {

    val points = Iterable((0.0,Color(255,0,0)), (1.0,Color(0,0,255)))
    assert(Visualization.interpolateColor(points, 0.5) == Color(128,0,128))
  }

  test("visualize") {
    val colors = Iterable((-85.94628003027694,Color(255,0,0)), (-1.0,Color(0,0,255)))
    val t = Iterable((Location(45.0,-90.0),-85.94628003027694), (Location(-45.0,0.0),-1.0))
    val image = Visualization.visualize(t, colors)
   // image.output(new java.io.File("target/some-image.png"))
  }


}
