package lolski

import scala.collection.mutable
import scala.math.Ordering

/**
  * Created by lolski on 3/27/16.
  */
class ScalaQueue[T](ord: Ordering[T]) {
  val q  = new mutable.PriorityQueue[T]()(ord) // order by value, ascending
  def notEmpty: Boolean = q.nonEmpty
  def enqueue(e: T) = q.enqueue(e)
  def dequeue() = q.dequeue()
  def size = q.size

}