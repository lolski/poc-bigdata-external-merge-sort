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
  *    - Minimize number of I/O accesses
  *    - Optimize I/O using buffering and NIO
  *    - Minimize memory buffer
  *
  * Sketch:
  *  - can we use Scala's internal sorting library?
  *
  * Trial:
  *  - .sorted on collection won't work because it'll use up all of the available memory
  *
  *
  */

object Main {

  private def main(args: Array[String]): Unit = {
    // input
    val tmp = "/Users/lolski/Playground/tremorvideo-problem1-part1/in"
    val in = s"${tmp}/in.txt"
    val out = s"${tmp}/out.txt"
    prepareInput(in)
    doSort(in, tmp, out)
  }

  def prepareInput(in: String): Unit = {
    println("writing input...")
    Helper.writeReversed(1, 40, in)
    println("done.")
  }

  def doSort(in: String, tmp: String, out: String): Unit = {
    println("starting sort procedure...")
    val is = Helper.readLines(in)

    println("done.")
  }
}
