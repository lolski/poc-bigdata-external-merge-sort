package lolski

import scala.concurrent.{ExecutionContext, Future}

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

  def elapsedAsync[T](block: => Future[T])(implicit ec: ExecutionContext): Future[(T, Long)] = {
    val t1 = System.currentTimeMillis()
    block map { res =>
      val t2 = System.currentTimeMillis()
      (res, t2 - t1)
    }
  }
}
