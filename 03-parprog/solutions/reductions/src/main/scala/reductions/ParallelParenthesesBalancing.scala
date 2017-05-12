package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {

    var i = 0
    val length = chars.length
    var acc = 0
    while (i < length & acc >= 0) {
      val addition = if (chars(i) == '(') 1 else if (chars(i) == ')') -1 else 0
      acc = acc + addition
      i = i + 1
    }
    acc == 0

   /*
    def loop(acc: Int, chars: Array[Char]): Int = {

      if (chars.isEmpty || acc < 0) acc
      else {
        val addition = chars.head match {
          case '(' => 1
          case ')' => -1
          case  _  => 0
        }

        loop(acc + addition, chars.tail)
      }

    }

    loop(0, chars) == 0
    */
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int): (Int, Int) = {
      var i = idx
      var leftAcc = arg1
      var rightAcc = arg2
      while (i < until) {
        if (chars(i) == '(') {
          leftAcc = leftAcc + 1
        }
        else if (chars(i) == ')') {
          if (leftAcc > 0) {
            leftAcc = leftAcc - 1
          } else {
            rightAcc = rightAcc + 1
          }
        }
        i = i + 1
      }
      // ')'       '('
      (rightAcc, leftAcc)
    }

    def reduce(from: Int, until: Int): (Int, Int) = {
      if (until - from <= threshold) {
        traverse(from, until, 0, 0)
      } else {
        val mid = from + (until - from)/2
        val (a1, a2) = parallel(reduce(from, mid), reduce(mid, until))
        //  [#')', #'(']   [#')', #'(')
        if (a1._2 >= a2._1) {
          (a1._1, a1._2 - a2._1 + a2._2)
        } else {
          (a1._1 + a2._1 - a1._2, a2._2)
        }

      }
    }

    reduce(0, chars.length) == (0, 0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
