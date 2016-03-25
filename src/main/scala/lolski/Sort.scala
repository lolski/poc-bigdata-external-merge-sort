package lolski

/**
  * Created by lolski on 3/25/16.
  */

object Sorter {
  def sort(in: String, tmp: String, out: String, linesPerChunk: Int, parallelism: Int): Unit = {
    // split file into n
    val rawChunks = split(in, linesPerChunk, tmp)

    // sort in memory
    val sortedChunks = rawChunks map { path =>
      val lines = Helper.readLines(path).toArray.map(_.toInt)
      (lines.sorted.map(_.toString), path)
    }

    // write to chunk
    val s = sortedChunks map { case (sorted, path) =>
      Helper.writeSeq(sorted, path, true)
    }

    // merge
    merge(s, out)
  }

  def split(in: String, linesPerChunk: Int, tmp: String): Seq[String] = {
    val lines = Helper.readLines(in)
    val chunked = lines.grouped(linesPerChunk).zipWithIndex

    val chunks = chunked map { case (chunk, id) =>
      val out = s"$tmp/chunk-$id"
      Helper.writeSeq(chunk, out, true)
      out
    }

    chunks toList
  }

  def merge(chunks: Seq[String], out: String): String = {
    val readers = chunks map { chunk => Helper.readLines(chunk) }
    readers
    out
  }
}
