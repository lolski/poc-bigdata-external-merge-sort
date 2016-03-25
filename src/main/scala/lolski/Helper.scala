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
      in.write(s"$e\n")
    }
  }

  def writeShuffled(from: Int, to: Int, path: String): Unit = overwrite(path) { in =>
    val numbers = shuffledOrder(from, to)
    numbers.foreach { e =>
      in.write(s"$e\n")
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
  def shuffledOrder(from: Int, to: Int) = scala.util.Random.shuffle(ascOrder(from, to))

  def ascOrder(from: Int, to: Int) = from to to

  def descOrder(from: Int, to: Int) = ascOrder(from, to).reverseIterator

  // file writing helpers
  def overwrite[T](path: String)(block: BufferedWriter => T): T = {
    Files.deleteIfExists(Paths.get(path))
    withWriter(new BufferedWriter(new FileWriter(path, true))) { in =>
      block(in)
    }
  }

  def withWriter[T](res: BufferedWriter)(block: BufferedWriter=> T) = {
    try block(res)
    finally res.close()
  }

  def readLines(path: String) = {
    val in = io.Source.fromFile(path)
    in.getLines()
  }
}
