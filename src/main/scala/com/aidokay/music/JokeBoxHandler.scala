package com.aidokay.music

import com.aidokay.music.JokeBox.*
import com.aidokay.music.JokeBoxData.JokeBoxContext
import com.aidokay.music.tracks.AudioProvider

class JokeBoxHandler(audioProvider: AudioProvider[String]) {

  private val jokeBoxData = new JokeBoxContext(audioProvider)
  private val EMPTY_ARRAY = Array[Byte]()

  def streamAudioChunk: Iterator[Array[Byte]] =
    new Iterator[Array[Byte]] {
      override def hasNext: Boolean = true

      override def next(): Array[Byte] = {
        if (jokeBoxData.state() == Paused) return EMPTY_ARRAY
        jokeBoxData.currentTrack() match {
          case None =>
            if (jokeBoxData.isEmpty) jokeBoxData.updateCurrentState(Paused)
            else jokeBoxData.playNext()
            EMPTY_ARRAY
          case Some(playing) =>
            if (playing.track.isDone && jokeBoxData.isEmpty) {
              jokeBoxData.stopPlaying()
              EMPTY_ARRAY
            } else {
              val chunk = playing.chunks.next()
              if (chunk.isEmpty) {
                playing.track.close()
                jokeBoxData.stopPlaying()
              }
              chunk
            }
        }
      }
    }

  def play(): Unit = {
    jokeBoxData.updateCurrentState(Playing)
  }
  def pause(): Unit = {
    jokeBoxData.updateCurrentState(Paused)
  }

  def list(): List[String] = {
    audioProvider.audioList
  }

  def schedule(csvTracks: String): Unit = {
    if (csvTracks.contains("all")) {
      jokeBoxData.allAudios()
    } else {
      asList(csvTracks).foreach(jokeBoxData.offer)
    }
  }

  def asList(str: String): List[String] =
    str.split(",").toList

}
