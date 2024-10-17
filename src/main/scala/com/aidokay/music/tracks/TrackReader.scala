package com.aidokay.music.tracks

import java.io.{BufferedInputStream, FileInputStream}
import java.nio.file.Paths
import scala.util.{Failure, Success, Try}

class TrackReader(location: String, music: String) {

  def asIterator(): Iterator[Array[Byte]] = new Iterator[Array[Byte]] {

    var eof: Boolean = false

    var readSize: Int  = 0
    override def hasNext: Boolean = !eof
    val bis = new BufferedInputStream(
      new FileInputStream(Paths.get(location, music).toFile)
    )
    val trackSize: Int = bis.available()
    var buff: Seq[Array[Byte]] = LazyList
      .from {
        new IterableOnce[Array[Byte]] {
          override def iterator: Iterator[Array[Byte]] = {
            new Iterator[Array[Byte]] {
              var hasMore = true
              var prepercent: Int = 0
              override def hasNext: Boolean = hasMore
              def showReadPercentage(readCount: Int) : Unit ={
                readSize += readCount
                val percent = ((readSize.doubleValue / trackSize.doubleValue) * 100).intValue()
                if (percent % 10 == 0 && percent != prepercent) {
                  printf("Has read %d%%\n", percent)
                  prepercent = percent
                }
              }
              override def next(): Array[Byte] = {
                val ba: Array[Byte] = new Array[Byte](4096 * 4)
                val count = bis.read(ba)
                if (count <= 0) {
                  hasMore = false
                  Array.empty[Byte]
                } else {
                  showReadPercentage(count)
                  ba
                }
              }
            }
          }
        }
      }
      .takeWhile(_.length > 0)

    private def chunk(): Array[Byte] = {
      val head = buff.head
      buff = buff.tail
      head
    }
    private def complete(): Array[Byte] = {
      eof = true
      Array.empty[Byte]
    }
    private def closeIfDone(): Unit = {
      if (eof) bis.close()
    }
    override def next(): Array[Byte] = {
      val result = Try {
        if (buff.head.length > 0) chunk() else complete()
      } match {
        case Success(value) => value
        case Failure(_)     => complete()
      }
      closeIfDone()
      result
    }
  }
}
