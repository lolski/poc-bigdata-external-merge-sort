package lolski

/**
  * Created by lolski on 3/26/16.
  */

object Timer {
  def elapsed[T](block: => T): (T, Long) = {
    val t1 = System.currentTimeMillis()
    val res = block
    val t2 = System.currentTimeMillis()
    (res, t2 - t1)
  }
}
