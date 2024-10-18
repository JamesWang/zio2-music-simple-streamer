package com.aidokay.music.tracks

import com.aidokay.music.MusicConf


object MusicProviders {
  def  mp3Provider(musicConf: MusicConf): AudioProvider[String] =
    new AudioProvider[String]() {
      override lazy val audioList: List[String] = TracksFinder.map3FileFinder.load(musicConf.location)

      override val location: String = musicConf.location
    }
}
