import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._


val l = List(1,2,3, 4)

(l, l.tail).zipped.toList

