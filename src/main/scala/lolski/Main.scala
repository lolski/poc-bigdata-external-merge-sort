package lolski

import java.util.concurrent._
import scala.concurrent.{Future, ExecutionContext}

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
    println(s"start sorting file ${AppConf.in}...")

    val async = ExternalMergeSort.sort(
      input         = AppConf.in,
      tmpDir        = AppConf.baseTmp,
      output        = AppConf.out,
      linesPerChunk = AppConf.linesPerChunk)

    async onSuccess { case _ =>
      println(s"sorted file saved in ${AppConf.out}")
    }
  }
}
