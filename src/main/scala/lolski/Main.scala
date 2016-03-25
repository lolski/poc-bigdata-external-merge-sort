package lolski

import java.io.{BufferedInputStream, FileWriter}
import java.nio.file.{Paths, Files}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by lolski on 3/24/16.
  * Problem set 1, part 1
  * Main assumptions:
  *  - input size is 300 million, ∫therefore won't fit in main memory
  *  - input from file, I/O needed
  *
  * Design decisions:
  *  - external sorting is used for input containing 300 million integers
  *    - Minimize number of I/O accesses (e.g., by minimizing the number of reading / writing pass)
  *    - Optimize I/O using buffering and NIO
  *    - Minimize memory buffer
  *    - Maximize cache efficiency
  *
  * Implementation:
  *  - we decide on using external merge sort
  *
  *
  *
  */

object Main {

  def main(args: Array[String]): Unit = {
    // input
    val tmp = "/Users/lolski/Playground/tremorvideo-problem1-part1/in"
    val in = s"${tmp}/in.txt"
    val out = s"${tmp}/out.txt"
    prepareInput(in)
    doSort(in, tmp, out)
    doVerify(out)
  }

  def prepareInput(in: String): Unit = {
    println("writing input...")
    Helper.writeReversed(1, 40, in)
    println("done.")
  }

  def doSort(in: String, tmp: String, out: String): Unit = {
    println("starting sort procedure...")
    Sorter.sort(in, tmp, out, 1, 1)
    println("done.")
  }

  def doVerify(in: String): Unit = {
    println(s"verification: is in asc order: ${Helper.isAscOrdered(in)}")
  }
}
