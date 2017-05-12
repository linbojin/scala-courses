package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers {

  test("makeGrid") {
    val t = Iterable((Location(1.0, 3.0), 10.0),
      (Location(1.0, 2.0), 20.0))
    val f = Manipulation.makeGrid(t)
    assert(f(2, 2) == 17.386932367760892)
    assert(f(3, 3) == 14.171900347568307)
  }

  test("average") {
    val t = Iterable(Iterable((Location(1.0, 3.0), 10.0),
      (Location(1.0, 2.0), 20.0)),
      Iterable((Location(2.0, 3.0), 100.0),
        (Location(5.0, 2.0), 200.0)))
    val f = Manipulation.average(t)
    assert(f(2, 2) == 60.476035225583274)
    assert(f(3, 3) == 61.196548696253856)
    assert(f(5, 5) == 88.9228936510913)
  }

  test("deviation") {
    //    val ys = 1975 to 2015
    //    ys.par.foreach{ cy => {
    //      val rs = Extraction.locateTemperatures(cy, "/stations.csv" ,s"/$cy.csv")
    //      println(s"Computing locationYearlyAverageRecords")
    //      val lt = Extraction.locationYearlyAverageRecords(rs)
    //      val c = Iterable(
    //        (60.0, Color(255,255,255)),
    //        (32.0, Color(255,0,0)),
    //        (12.0, Color(255,255,0)),
    //        (0.0,  Color(0,255,255)),
    //        (-15.0,Color(0,0,255)),
    //        (-27.0,Color(255,0,255)),
    //        (-50.0,  Color(33,0,107)),
    //        (-60.0,  Color(0,0,0))
    //      )
    //      val yearlyData = Iterable((cy, lt))
    //      def generateImage(year: Int, z: Int, x: Int, y: Int, data: Iterable[(Location, Double)]): Unit = {
    //
    //        println(s"Generating tile $z $x $y")
    //        val image = Interaction.tile(data, c, z, x, y)
    //        println(s"Generating $year/$z/$x-$y.png")
    //        image.output(new java.io.File(s"target/temperatures/$year/$z/$x-$y.png"))
    //      }
    //      Interaction.generateTiles(yearlyData, generateImage)
    //    }}

  }

}