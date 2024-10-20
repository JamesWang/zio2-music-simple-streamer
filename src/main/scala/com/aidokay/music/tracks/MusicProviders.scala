package com.aidokay.music.tracks

import com.aidokay.music.MusicConf
import zio.{ZIO, ZLayer}

object MusicProviders {
  private def mp3Provider(musicConf: MusicConf): AudioProvider[String] =
    new AudioProvider[String]() {
      override lazy val audioList: List[String] =
        TracksFinder.map3FileFinder().load(musicConf.location)

      override val location: String = musicConf.location
    }

  val mp3: ZLayer[MusicConf, Nothing, AudioProvider[String]] =
    ZLayer.fromZIO(ZIO.serviceWith[MusicConf](mp3Provider))
}
