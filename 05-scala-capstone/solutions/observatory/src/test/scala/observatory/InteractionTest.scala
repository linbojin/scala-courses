package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  test("tileLocation") {
    assert(Interaction.tileLocation(12, 100, 400) == Location(80.87282721505684, -171.2109375))
  }

  test("tile") {
    val t = Iterable((Location(45.0,-90.0),5.0), (Location(-45.0,0.0),30.0))
    val c = Iterable((5.0,Color(255,0,0)), (30.0,Color(0,0,255)))
    val image = Interaction.tile(t, c, 0, 0, 0)
    image.output(new java.io.File("target/some-image.png"))
  }

//  test("generateTiles"){
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
//  }
}
