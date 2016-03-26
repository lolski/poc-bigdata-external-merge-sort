package lolski

import java.nio.file.{Paths, Files}

import scala.collection._

/**
  * Created by lolski on 3/25/16.
  */

object Sorter {
  def sort(in: String, tmp: String, out: String, linesPerChunk: Int, parallelism: Int): Unit = {
    // split file into n
    val (rawChunks, t1) = Timer.elapsed {
      split(in, linesPerChunk, tmp)                                                                       // 1 read, 1 write
    }

    println(s"splitting took ${t1}ms / ${t1 / 1000.0}s")

    val (sortedChunks, t2) = Timer.elapsed {
      // sort in memory
      // must limit the number of instance being processed concurrently to prevent OutOfMemoryException
      val tmp = rawChunks map { path =>
        val lines = IO.readLines(path).toArray.map(_.toInt)                                               // 1 read
        (lines.sorted.map(_.toString), path)
      }

      // write to chunk
      tmp map { case (sorted, path) =>
        IO.writeSeq(sorted, path, true)                                                                  // 1 write
      }
    }

    println(s"local sorting took ${t2}ms / ${t2 / 1000.0}s")

    // merge
    val (_, t3) = Timer.elapsed(merge(sortedChunks, out, 100)) // how many reads & writes?
    println(s"merging took ${t3}ms / ${t3 / 1000.0}s")

    sortedChunks.foreach { path => Files.delete(Paths.get(path)) }
  }

  def split(in: String, linesPerChunk: Int, tmp: String): Seq[String] = {
    val lines = IO.readLines(in)
    val chunked = lines.grouped(linesPerChunk).zipWithIndex

    val chunks = chunked map { case (chunk, id) =>
      val out = s"$tmp/chunk-$id"
      IO.writeSeq(chunk, out, true)
      out
    }

    chunks toList
  }

  def merge(chunks: Seq[String], out: String, linesPerChunk: Int): String = {
    // instrumentation
    var sorting      = 0L
    var ioAccess     = 0L
    var condChecking = 0L

    // initialize variables
    val pq = new mutable.PriorityQueue[(Int, Int)]()(Ordering.by { case (v, i) => -v}) // order by value, ascending
    val chunkIterators = chunks map { chunk =>
      val it = IO.readLines(chunk)
      val indexed = it.zipWithIndex // add index to help merging
      indexed
    }

    // read first line from all chunks into (value, index)
    val (lines, io1) = Timer.elapsed {
      chunkIterators flatMap { it =>
        val tmp = it.take(linesPerChunk).map { case (v, i) => (v.toInt, i) }
        tmp toSeq
      }
    }
    ioAccess += io1

    val (_, s1) = Timer.elapsed(lines foreach { e => pq.enqueue(e) }) // sort in memory using priority queue
    sorting += s1

    println("pq.sz = " + pq.size)
    IO.overwrite(out) { writer =>
      def checkAndMeasure() = {
        val (exists, c1) = Timer.elapsed(chunkIterators.exists(_.hasNext))
        condChecking += c1
        exists || pq.nonEmpty
      }

      while (checkAndMeasure()) {
        val ((v1, i1), s2) = Timer.elapsed(pq.dequeue())
        sorting += s2

        val (_, io2) = Timer.elapsed {
          writer.write(s"$v1")
          writer.newLine()
        }
        ioAccess += io2

        val (next, io3) = Timer.elapsed(chunkIterators(i1).take(linesPerChunk).map { case (v, i) => (v.toInt, i) }.toSeq)
        ioAccess += io3

        val (_, s3) = Timer.elapsed(next foreach { e => pq.enqueue(e) })
        sorting += s3
      }
    }

    println(s"cond took ${condChecking}ms / ${condChecking / 1000.0}s")
    println(s"sorting took ${sorting}ms / ${sorting / 1000.0}s")
    println(s"IO took ${ioAccess}ms / ${ioAccess / 1000.0}s")
    out
  }
}
