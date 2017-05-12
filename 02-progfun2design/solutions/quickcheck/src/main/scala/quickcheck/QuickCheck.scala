package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = for {
    k <- arbitrary[A]
    m <- oneOf(const(empty), genHeap)
  } yield insert(k, m)
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)


  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("min1") = forAll { a: A =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("minOfTwo") = forAll { (a: A, b: A) => {
      val h = insert(a, insert(b, empty))
      if (ord.lteq(a, b)) {
        findMin(h) == a
      } else {
        findMin(h) == b
      }
    }
  }

  property("insertOne") = forAll { (a: A) => {
    val h = deleteMin(insert(a, empty))
      isEmpty(h)
   }
  }

  property("test") = forAll { (a: A, b: A, h: H) => {
    val h1 = insert(a, insert(b, h))
    val h2 = insert(b, insert(a, h))

    findMin(h1) == findMin(h2)

  }
  }


  property("minOfTwo") = forAll { (a: H, b: H) => {
      val min = findMin(meld(a, b))
      val minA = findMin(a)
      val minB = findMin(b)
      if (ord.lteq(minA, minB)) {
        min == minA
      } else {
        min == minB
      }
    }
  }

  property("sorted") = forAll { (h: H) => {
    def loop(h: H, acc: List[A]): List[A] = {
      if (isEmpty(h)) {
        acc
      } else {
        val a = findMin(h)
        val newH = deleteMin(h)
        loop(newH, a :: acc)
      }
    }
    val l = loop(h, List())

    (l, l.tail).zipped.forall((a, b) => ord.gteq(a, b))
  }
  }

  // Take two arbitrary heaps, meld together. Then remove min from 1 and insert into 2, meld the results. Compare two melds by comparing sequences of ranks.
  property("meldMinMove") = forAll { (h1: H, h2: H) =>
    def remMin(ts: H, as: List[Int]): List[Int] = {
      if (isEmpty(ts)) as
      else findMin(ts) :: remMin(deleteMin(ts), as)
    }
    val meld1 = meld(h1, h2)
    val min1 = findMin(h1)
    val meld2 = meld(deleteMin(h1), insert(min1, h2))
    val xs1 = remMin(meld1, Nil)
    val xs2 = remMin(meld2, Nil)
    xs1 == xs2
  }

}
