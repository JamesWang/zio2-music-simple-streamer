package com.aidokay.music.tracks


object MusicProviders {
  def  mp3Provider(loc: String): AudioProvider[String] =
    new AudioProvider[String]() {
      override lazy val audioList: List[String] = TracksFinder.map3FileFinder.load(loc)

      override val location: String = loc
    }
}
