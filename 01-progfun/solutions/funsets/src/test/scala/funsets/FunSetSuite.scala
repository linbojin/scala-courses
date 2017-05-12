package funsets

import org.scalatest.FunSuite


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * This class is a test suite for the methods in object FunSets. To run
 * the test suite, you can either:
 *  - run the "test" command in the SBT console
 *  - right-click the file in eclipse and chose "Run As" - "JUnit Test"
 */
@RunWith(classOf[JUnitRunner])
class FunSetSuite extends FunSuite {

  /**
   * Link to the scaladoc - very clear and detailed tutorial of FunSuite
   *
   * http://doc.scalatest.org/1.9.1/index.html#org.scalatest.FunSuite
   *
   * Operators
   *  - test
   *  - ignore
   *  - pending
   */

  /**
   * Tests are written using the "test" operator and the "assert" method.
   */
  // test("string take") {
  //   val message = "hello, world"
  //   assert(message.take(5) == "hello")
  // }

  /**
   * For ScalaTest tests, there exists a special equality operator "===" that
   * can be used inside "assert". If the assertion fails, the two values will
   * be printed in the error message. Otherwise, when using "==", the test
   * error message will only say "assertion failed", without showing the values.
   *
   * Try it out! Change the values so that the assertion fails, and look at the
   * error message.
   */
  // test("adding ints") {
  //   assert(1 + 2 === 3)
  // }


  import FunSets._

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
  }

  /**
   * This test is currently disabled (by using "ignore") because the method
   * "singletonSet" is not yet implemented and the test would fail.
   *
   * Once you finish your implementation of "singletonSet", exchange the
   * function "ignore" by "test".
   */
  test("singletonSet(1) contains 1") {

    /**
     * We create a new instance of the "TestSets" trait, this gives us access
     * to the values "s1" to "s3".
     */
    new TestSets {
      /**
       * The string argument of "assert" is a message that is printed in case
       * the test fails. This helps identifying which assertion failed.
       */
      assert(contains(s1, 1), "Singleton")
    }
  }

  test("union contains all elements of each set") {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
    }
  }

  test("intersect contains the elements that are both in give sets") {
    new TestSets {
      val unionSet = union(s1, s2)
      val intersectSet = intersect(unionSet, s2)
      assert(!contains(intersectSet, 1), "remove 1")
      assert(contains(intersectSet, 2), "keep 2")
    }
  }

  test("diff contains all elements of 1st set that are not in 2nd set") {
    new TestSets {
      val unionSet = union(s1, s2)
      val diffSet = diff(unionSet, s2)
      assert(contains(diffSet, 1), "keep 1")
      assert(!contains(diffSet, 2), "remove 2")
    }
  }

  test("filter contains the subset of given set which are accepted by a given predicate p") {
    new TestSets {
      val unionSet = union(s1, s2)
      val subSet = filter(unionSet, (x: Int) => x < 2)
      assert(contains(subSet, 1), "filter keeps 1")
      assert(!contains(subSet, 2), "filter removes 2")
      assert(!contains(subSet, 0), "filter not add new element")
    }
  }


  trait TestSets2 {
    val s1 = (x: Int) => (x>0 && x<4)
  }

  test("forall return whether all bounded integers within given set satisfy given predicate p") {
    new TestSets2 {
      assert(forall(s1, (x: Int) => x < 5), "all value small than 5")
      assert(!forall(s1, (x: Int) => x == 3), "only one value equals to 4")
    }
  }

  test("exists returns whether there exists a bounded integer within given set that satisfies give predicate p") {
    new TestSets2 {
      assert(exists(s1, (x: Int) => x < 5), "there are values smaller than 5")
      assert(exists(s1, (x: Int) => x == 3), "there is one value equal to 3")
      assert(!exists(s1, (x: Int) => x > 4), "no value larger than 4")
    }
  }

  test("map returns a set transformed by applying given function to each element of given set") {
    new TestSets2 {
      val s = map(s1, x => x * x)
      assert(contains(s, 1), "1 => 1")
      assert(contains(s, 4), "2 => 4")
      assert(contains(s, 9), "3 => 9")
      assert(!contains(s, 2), "2 is removed")
    }
  }

  trait TestSets3 {
    val s1 = (x: Int) => (x>998 && x<1002)
    val s2 = (x: Int) => (x> -1002 && x< -998)
  }

  test("forall should handle bound cases") {
    new TestSets3 {
      assert(!forall(s1, (x: Int) => x < 1000))
      assert(forall(s1, (x: Int) => x < 1001))
      assert(forall(s2, (x: Int) => x > -1001))
      assert(!forall(s2, (x: Int) => x < -1000))
    }
  }

}
