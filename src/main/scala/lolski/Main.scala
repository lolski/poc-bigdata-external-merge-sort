package lolski

import java.util.concurrent._
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

object AppConf {
  // input
  val baseTmp       = s"${IO.getCwd}/files"
  val in            = s"${baseTmp}/in.txt" // input file (unsorted)
  val out           = s"${baseTmp}/out.txt" // output file (sorted)

  // sorting params
  val start         = 1
  val stop          = 10004
  val linesPerChunk = 103
  val parallelism   = 8

}

object Main {
  val threadPool = Executors.newFixedThreadPool(AppConf.parallelism)
  implicit val parallelSortEC = ExecutionContext.fromExecutor(threadPool)

  def main(args: Array[String]): Unit = {
    doWriteInput(AppConf.in)

    val async = doSort(AppConf.in, AppConf.baseTmp, AppConf.out)

    async onSuccess { case _ =>
      doVerify(AppConf.out)
    }
  }

  def doWriteInput(in: String): Unit = {
    println("writing input started...")
    IO.writeShuffled(AppConf.start, AppConf.stop, in)
    println("writing input done.")
  }

  def doSort(in: String, tmp: String, out: String)(implicit ec: ExecutionContext): Future[Unit] = {
    println("sort procedure started...")
    val async = Sort.sort(in, tmp, out, AppConf.linesPerChunk)
    async map { _ => println("sort procedure done.") }
  }

  def doVerify(in: String): Unit = {
    val (h, it) = IO.readLines(in)
    val ascending = Tests.isAscIncrement(it) //
    println(s"verify if output is in ascending order: ${ascending}")
    h.close()
  }
}
