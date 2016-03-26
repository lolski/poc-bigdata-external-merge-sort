package lolski

import scala.collection._

/**
  * Created by lolski on 3/25/16.
  */

object Sorter {
  def sort(in: String, tmp: String, out: String, linesPerChunk: Int, parallelism: Int): Unit = {
    // split file into n
    val rawChunks = split(in, linesPerChunk, tmp)                                                         // 1 read, 1 write

    // sort in memory
    // must limit the number of instance being processed concurrently to prevent OutOfMemoryException
    val sortedChunks = rawChunks map { path =>
      val lines = Helper.readLines(path).toArray.map(_.toInt)                                             // 1 read
      (lines.sorted.map(_.toString), path)
    }

    // write to chunk
    val s = sortedChunks map { case (sorted, path) =>
      Helper.writeSeq(sorted, path, true)                                                                 // 1 write
    }

    // merge
    merge(s, out)                                                                                         // how many reads & writes?
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
    // initialize variables
    println("initializing merge...")
    val pq = new mutable.PriorityQueue[(Int, Int)]()(Ordering.by { case (v, i) => -v}) // order by value, ascending
    val chunkIterators = chunks map { chunk => Helper.readLines(chunk).zipWithIndex }

    // read first line from all chunks into (value, index)
    val lines = chunkIterators flatMap { e => e.take(1).map { case (v, i) => (v.toInt, i) }.toSeq }
    lines foreach { e => pq.enqueue(e) } // prioritize
    println("heap size = " + pq.size)
    println("there are more elements to process = " + chunkIterators.filter(_.hasNext))
    Helper.overwrite(out) { writer =>
      while (chunkIterators.exists(_.hasNext) || pq.size > 0) {
        val (v1, i1) = pq.dequeue()
        writer.write(s"$v1")
        writer.newLine()
        val next = chunkIterators(i1).take(1).map { case (v, i) => (v.toInt, i) }.toSeq
        next foreach { e => pq.enqueue(e) }
      }
    }

    out
  }
}
