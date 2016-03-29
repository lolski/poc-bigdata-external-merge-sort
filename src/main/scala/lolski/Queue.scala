package lolski

import java.util.PriorityQueue

/**
  * Created by lolski on 3/29/16.
  */

object Queue {
  class ScalaQueue[T](ord: Ordering[T]) {
    val q  = new scala.collection.mutable.PriorityQueue[T]()(ord) // order by value, ascending
    def notEmpty: Boolean = q.nonEmpty
    def enqueue(e: T) = q.enqueue(e)
    def dequeue() = q.dequeue()
    def size = q.size

  }

  // not used
  class JavaQueue[T] {
    val q = new PriorityQueue[T]()
    def notEmpty: Boolean = q.size > 0
    def enqueue(e: T) = q.add(e)
    def dequeue() = q.poll()
    def size = q.size
  }
}
