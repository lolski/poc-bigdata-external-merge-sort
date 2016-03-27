package lolski

/**
  * Created by lolski on 3/27/16.
  */
object Tests {
  // comparison helpers
  def isAscOrdered(in: Iterator[String]): Boolean = {
    val sliding = in.sliding(2, 1)
    val sorted = sliding forall { case Seq(prev, cur) => prev.toInt < cur.toInt }
    sorted
  }

  def isAscIncrement(in: Iterator[String]): Boolean = {
    val sliding = in.sliding(2, 1)
    val sorted = sliding forall { case Seq(prev, cur) => prev.toInt == (cur.toInt - 1) }
    sorted
  }
}
