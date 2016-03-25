package lolski

import java.io.{FileWriter, BufferedWriter, PrintWriter}

/**
  * Created by lolski on 3/25/16.
  */


object Sorter {
  def sort(in: String, tmp: String, out: String, linesPerChunk: Int, parallelism: Int): Unit = {
    // split file into n
    val rawChunks = split(in, linesPerChunk, tmp)

    rawChunks foreach { chunk =>
      val lines = Helper.readLines(chunk).toArray.map(_.toInt)
      val sorted = lines.sorted
      Helper.overwrite(chunk) { writer =>
        sorted foreach { line =>
          writer.write(s"$line")
          writer.newLine()
        }
      }
    }
  }

  // division can be done in two ways: linesPerChunk or
  def split2(in: String, linesPerChunk: Int, tmp: String): Seq[String] = {
    val lines = Helper.readLines(in)
    var id = 0
    var currentLine = 0
    var out = s"$tmp/chunk-$id"
    var writer: BufferedWriter = new BufferedWriter(new FileWriter(out))
    val outs = scala.collection.mutable.ListBuffer.empty[String]

    lines foreach { line =>
      println(line)
      currentLine += 1

      writer.write(line)
      writer.newLine()

      if (currentLine > linesPerChunk-1) {
        println(s"closing writer $id")
        writer.close()
        outs += out
        // next
        if (lines.hasNext) {
          currentLine = 0
          id += 1
          out = s"$tmp/chunk-$id"
          writer = new BufferedWriter(new FileWriter(out))
        }
      }
    }

    outs.toSeq
  }

  def split(in: String, linesPerChunk: Int, tmp: String): Seq[String] = {
    val lines = Helper.readLines(in)

    val written = lines.grouped(linesPerChunk).zipWithIndex.map { case (chunk, id) =>
      val out = s"$tmp/chunk-$id"
      val writer: BufferedWriter = new BufferedWriter(new FileWriter(out))
      chunk.foreach { num =>
        writer.write(s"$num")
        writer.newLine()
      }
      writer.close()
      out
    }
    written toSeq
  }
}
