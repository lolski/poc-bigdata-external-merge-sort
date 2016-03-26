package lolski

import java.io.{BufferedInputStream, FileWriter}
import java.nio.file.{Paths, Files}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by lolski on 3/24/16.
  * Problem set 1, part 1
  * Main assumptions:
  *  - input size is 300 million integer, requires 9.6GB space excluding overheads (int is 32 bit). won't fit in main memory
  *  - input from file, I/O needed
  *
  * Design decisions:
  *  - external sorting is used for input containing 300 million integers
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
  val stop = 20000
  val linesPerChunk = 1000
  val parallelism  = 1

  def main(args: Array[String]): Unit = {
    doWriteInput(in)
    doSort(in, tmp, out)
    doVerify(out)
  }

  def doWriteInput(in: String): Unit = {
    println("writing input...")
    IO.writeShuffled(start, stop, in)
    println("done.")
  }

  def doSort(in: String, tmp: String, out: String): Unit = {
    println("starting sort procedure...")
    Sorter.sort(in, tmp, out, linesPerChunk, parallelism)
    println("done.")
  }

  def doVerify(in: String): Unit = {
    val (h, it) = IO.readLines(in)
    val ascending = NumGenerator.isAscOrdered(it)
    println(s"verify if output is in ascending order: ${ascending}")
    h.close()
  }
}
