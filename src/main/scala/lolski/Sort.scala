package lolski

import scala.collection._

/**
  * Created by lolski on 3/25/16.
  */

object Sorter {
  def sort(in: String, tmp: String, out: String, linesPerChunk: Int, parallelism: Int): String = {
    import Timer.elapsed

    // split file into n
    val (chunks, t1) = elapsed(splitStep(in, linesPerChunk, tmp)) // 1 full pass to read and write
    println(s"splitting took ${t1}ms / ${t1 / 1000.0}s")

    // sort
    val (sortedChunks, t2) = elapsed(sortStep(chunks))
    println(s"local sorting took ${t2}ms / ${t2 / 1000.0}s")

    // merge
    val (out_, t3) = elapsed(mergeStep(sortedChunks, out, linesPerChunk))  // how many reads & writes?
    println(s"merging took ${t3}ms / ${t3 / 1000.0}s")

    // clean up
    IO.delete(chunks)

    out_
  }

  def splitStep(in: String, linesPerChunk: Int, tmp: String): Seq[String] = {
    val (handle, lines) = IO.readLines(in)
    val chunked = lines.grouped(linesPerChunk).zipWithIndex

    val chunks = chunked map { case (chunk, id) =>
      val out = s"$tmp/chunk-$id"
      IO.writeSeq(chunk, out, true)
      out
    }

    val res = chunks.toList
    handle.close()
    res
  }

  def sortStep(chunks: Seq[String]): Seq[String] = {
    // sort in memory
    // must limit the number of instance being processed concurrently to prevent OutOfMemoryException
    chunks map { path =>
      val (handle, lines) = IO.readLines(path) // 1 full pass to read
      val it = lines.toArray.map(_.toInt) // read
      handle.close() // clean up
      IO.writeSeq(it.sorted.map(_.toString), path, true) // 1 pass to write
    }
  }

  def mergeStep(chunks: Seq[String], out: String, linesPerChunk: Int): String = {
    // instrumentation
    import Timer.elapsed
    var elapsedSorting = 0L
    var elapsedIO      = 0L

    // initialize variables - priority queue, file readers
    val readers = chunks.zipWithIndex map { case (chunk, id) =>
      val (handle, it) = IO.readLines(chunk)
      // chunk id is used to idenfity which chunk reader to advance after dequeing an element from the queue
      val indexed = it map { e => (e, id) }
      (handle, indexed)
    } toVector
    val totalSize = readers.size
    var closedReaders = mutable.Set[Int]()
    val pQueue  =
      new mutable.PriorityQueue[(Int, Int)]()(Ordering.by { case (v, i) => -v}) // order by value, ascending

    // read first line from all chunks into (value, index)
    val (lines, io1) = elapsed {
      readers flatMap { case (h, it) =>
        val tmp = it.take(linesPerChunk).map { case (v, i) => (v.toInt, i) }
        tmp toSeq
      }
    }
    elapsedIO += io1

    val (_, s1) = elapsed {
      lines foreach { e =>
        pQueue.enqueue(e)
      } // sort in memory using priority queue
    }
    elapsedSorting += s1

    IO.overwrite(out) { writer =>
      while (closedReaders.size < totalSize || pQueue.nonEmpty) {
        val ((v1, i1), s2) = elapsed(pQueue.dequeue())
        elapsedSorting += s2

        // write to out
        val (_, io2) = elapsed {
          writer.write(s"$v1")
          writer.newLine()
        }
        elapsedIO += io2

        val (next, io3) = elapsed {
          readers(i1)
            ._2
            .take(linesPerChunk)
            .map { case (v, i) => (v.toInt, i) }
            .toSeq
        }
        elapsedIO += io3

        val (_, s3) = elapsed(next foreach { e => pQueue.enqueue(e) })
        elapsedSorting += s3

        val it     = readers(i1)._2
        val handle = readers(i1)._1
        if (it.isEmpty) {
          // close reader and put the id in closedReaders set
          val alreadyClosed = closedReaders.contains(i1)
          if (!alreadyClosed) {
            handle.close()
            closedReaders += i1
          }
          else {
            // do nothing, reader is already closed
          }
        }
      }
    }

    println(s"sorting took ${elapsedSorting}ms / ${elapsedSorting / 1000.0}s")
    println(s"IO took ${elapsedIO}ms / ${elapsedIO / 1000.0}s")
    out
  }
}
