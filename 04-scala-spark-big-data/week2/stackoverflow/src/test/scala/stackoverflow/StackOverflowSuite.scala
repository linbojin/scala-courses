package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.net.URL
import java.nio.channels.Channels
import java.io.File
import java.io.FileOutputStream

@RunWith(classOf[JUnitRunner])
class tackOverflowSuite extends FunSuite with BeforeAndAfterAll {


  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  test("the class should compute the median correctly") {
    assert(testObject.computeMedian(List(1, 2, 3)) == 2)
    assert(testObject.computeMedian(List(1, 2, 3, 4)) == 2)
    assert(testObject.computeMedian(List(1, 2, 4, 5)) == 3)
    assert(testObject.computeMedian(List.empty[Int]) == 0)
    assert(testObject.computeMedian(List(1)) == 1)
  }

  test("clusterResults"){
    val centers = Array((0,0), (100000, 0))
    val rdd = StackOverflow.sc.parallelize(List(
      (0, 1000),
      (0, 23),
      (0, 234),
      (0, 0),
      (0, 1),
      (0, 1),
      (50000, 2),
      (50000, 10),
      (100000, 2),
      (100000, 5),
      (100000, 10),
      (200000, 100),
      (100000, 2444),
      (100000, 5444),
      (100000, 10444),
      (200000, 100444)  ))
    testObject.printResults(testObject.clusterResults(centers, rdd))
//
//    Resulting clusters:
//      Score  Dominant language (%percent)  Questions
//      ================================================
//    12    JavaScript        (75.0 %)            8
//    1227  PHP               (75.0 %)            8
  }

}
