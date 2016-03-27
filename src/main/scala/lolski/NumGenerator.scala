package lolski

/**
  * Created by lolski on 3/25/16.
  */
object NumGenerator {
  // generating number helpers
  def shuffledOrder(start: Int, end: Int): Seq[Int] = scala.util.Random.shuffle(ascOrder(start, end))

  def ascOrder(start: Int, end: Int): Seq[Int] = start to end

  def descOrder(start: Int, end: Int): Seq[Int] = end to start by -1

}
