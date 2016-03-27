package lolski

import java.util.PriorityQueue

/**
  * Created by lolski on 3/27/16.
  */

// not used
class JavaQueue[T] {
  val q = new PriorityQueue[T]()
  def notEmpty: Boolean = q.size > 0
  def enqueue(e: T) = q.add(e)
  def dequeue() = q.poll()
  def size = q.size
}