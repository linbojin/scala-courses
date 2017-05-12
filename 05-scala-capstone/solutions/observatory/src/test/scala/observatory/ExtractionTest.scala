package observatory

import java.time.LocalDate

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExtractionTest extends FunSuite {

  test("locateTemperatures") {
    val r = Extraction.locateTemperatures(1975, "/stations.csv" ,"/1975.csv")
    assert(r.head.toString() == "(1975-01-01,Location(70.933,-8.667),-4.888888888888889)")
  }

  test("locationYearlyAverageRecords") {
    val records = Iterable((LocalDate.of(2017, 3, 1), Location(1.0, 2.0), 1.0),
      (LocalDate.of(2017, 4, 2), Location(1.0, 2.0),3.0),
        (LocalDate.of(2017, 3, 1), Location(1.0, 3.0),3.0))

    val r = Extraction.locationYearlyAverageRecords(records)
    assert(r.toList == List((Location(1.0,3.0),3.0), (Location(1.0,2.0),2.0)))
  }
}