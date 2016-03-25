package lolski

import java.io._
import java.nio.file.{Paths, Files}

/**
  * Created by lolski on 3/25/16.
  */
object Helper {
  def writeReversed(from: Int, to: Int, path: String): Unit = overwrite(path) { in =>
    val numbers = descOrder(from, to)
    numbers.foreach { e =>
      in.write(s"$e")
      in.newLine()
    }
  }

  def writeShuffled(from: Int, to: Int, path: String): Unit = overwrite(path) { in =>
    val numbers = shuffledOrder(from, to)
    numbers.foreach { e =>
      in.write(s"$e")
      in.newLine()
    }
  }

  // comparison helpers
  def isAscOrdered(path: String): Boolean = {
    val in = readLines(path)
    val sliding = in.sliding(2, 1)
    val sorted = sliding forall { case Seq(prev, cur) => prev < cur }
    sorted
  }

  // generating number helpers
  def shuffledOrder(start: Int, end: Int): Seq[Int] = scala.util.Random.shuffle(ascOrder(start, end))

  def ascOrder(start: Int, end: Int): Seq[Int] = start to end

  def descOrder(start: Int, end: Int): Seq[Int] = end to start by -1

  // file writing helpers
  def overwrite[T](path: String)(block: BufferedWriter => T): T = {
    Files.deleteIfExists(Paths.get(path))
    append(path) { in =>
      block(in)
    }
  }

  def append[T](path: String)(block: BufferedWriter => T): T = {
    withWriter(new BufferedWriter(new FileWriter(path, true))) { in =>
      block(in)
    }
  }

  def withWriter[T](res: BufferedWriter)(block: BufferedWriter=> T): T = {
    try block(res)
    finally res.close()
  }

  // TODO: add close method
  def readLines(path: String): Iterator[String] = {
    val in = io.Source.fromFile(path)
    in.getLines()
  }

  def writeSeq(numbers: Seq[String], out: String, shouldOverwrite: Boolean): String = {
    // pick overwrite or append function based on shouldOverwrite flag
    def fn[T]: String => (BufferedWriter => T) => T =
      if (shouldOverwrite) overwrite _ else append _

    fn(out) { writer =>
      numbers foreach { line =>
        writer.write(line)
        writer.newLine()
      }
    }

    out
  }
}
