package com.aidokay.music.tracks

import java.io.File

object TracksFinder {
  trait TrackLoader[F[_], T] {
    type O
    def load(loc: T): F[O]
  }

  trait MusicTrack[T] {
    def isMusic(t: T): Boolean
  }

  class MusicFileFilter extends MusicTrack[String] {
    def isMusic(name: String): Boolean = name.endsWith(".mp3")
  }
  class TrackFileLoader(musicFile: MusicTrack[String])
      extends TrackLoader[List, String] {
    override type O = String

    private def listOfFiles(loc: File): List[String] = {
      Option(loc.listFiles) match {
        case Some(files) =>
          files.map(_.getName).filter(musicFile.isMusic).toList
        case None => Nil
      }
    }

    override def load(loc: String): List[O] = listOfFiles(new File(loc))
  }

  val map3FileFinder = new TrackFileLoader(new MusicFileFilter())
}
