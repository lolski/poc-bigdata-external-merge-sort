package lolski

import java.io.{BufferedInputStream, FileWriter}
import java.nio.file.{Paths, Files}
import java.util.concurrent.ExecutorService

import scala.concurrent.{Future, ExecutionContext}

/**
  * Created by lolski on 3/24/16.
  * Problem set 1, part 1
  * Main assumptions:
  *  - input size is 300 million integer, requires 9.6GB space excluding overheads (int is 32 bit). won't fit in main memory
  *  - input from file, I/O needed
  *
  * Design decisions:
  *  - Output is written to a new file instead of overwriting the input.
  *    The reason is to prevent losing / corrupting the input file in case of failure (e.g., sorting crashed while in the middle of writing)
  *  - External sorting is used for input containing 300 million integers
  *    - Minimize number of I/O accesses (e.g., by minimizing the number of reading / writing pass)
  *    - Optimize I/O using buffering and / or NIO
  *    - Minimize memory buffer
  *    - Maximize cache efficiency
  *    - Minimize open file at a time
  *
  * Implementation:
  *  - we decide on using external merge sort
  */

object Main {
  // input
  val tmp = "/Users/lolski/Playground/tremorvideo-problem1-part1/in"
  val in = s"${tmp}/in.txt"
  val out = s"${tmp}/out.txt"

  // sorting params
  val start = 1
  val stop = 10000000
  val linesPerChunk = 10000
  val parallelism  = 2

  // val
  implicit val parallelSortEC = ExecutionContext.fromExecutor(java.util.concurrent.Executors.newFixedThreadPool(parallelism))

  def main(args: Array[String]): Unit = {
    doWriteInput(in)
    doSort(in, tmp, out) map { _ =>
      doVerify(out)
    }
  }

  def doWriteInput(in: String): Unit = {
    println("writing input started...")
    IO.writeShuffled(start, stop, in)
    println("writing input done.")
  }

  def doSort(in: String, tmp: String, out: String): Future[Unit] = {
    println("sort procedure started...")
    val async = Sorter.sort(in, tmp, out, linesPerChunk)
    async map { _ => println("sort procedure done.") }
  }

  def doVerify(in: String): Unit = {
    val (h, it) = IO.readLines(in)
    val ascending = NumGenerator.isAscOrdered(it)
    println(s"verify if output is in ascending order: ${ascending}")
    h.close()
  }
}
