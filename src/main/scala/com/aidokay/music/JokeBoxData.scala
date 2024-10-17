package com.aidokay.music

import com.aidokay.music.JokeBox.{JokeBoxState, Paused, Playing}
import com.aidokay.music.tracks.{AudioProvider, TrackReader}

import java.io.RandomAccessFile
import scala.collection.mutable

object JokeBoxData {
  class JokeBoxContext(private val audioProvider: AudioProvider[String]) {
    @volatile
    private var currentState: JokeBoxState = Paused
    private val playList: mutable.ArrayDeque[String] =
      new mutable.ArrayDeque[String]()

    def updateCurrentState(state: JokeBoxState): Unit = currentState = state

    def offer(track: String): Unit = {
      playList.append(track)
      currentState = Playing
    }

    def take(): Option[String] =
      if (playList.nonEmpty) Option(playList.remove(0)) else None

    def allAudios(): Unit = {
      playList.addAll(audioProvider.audioList)
      ()
    }

    def state(): JokeBoxState = currentState

    def isEmpty: Boolean = playList.isEmpty

    private var currentTrackOpt: Option[PlayingInfo] = None
    case class PlayingInfo(track: PlayingTrack, chunks: Iterator[Array[Byte]])

    def currentTrack(): Option[PlayingInfo] = currentTrackOpt

    def playNext(): Unit = {
      take() match {
        case Some(music) =>
          println(s"\nPlaying [$music...]")
          val track = new PlayingTrack(music, audioProvider.location)
          currentTrackOpt = Some(
            PlayingInfo(
              track,
              track.streamAudioChunk()
            ))
          currentState = Playing
        case _ =>
      }
    }

    def stopPlaying(): Unit = {
      currentTrackOpt = None
    }

  }

  class PlayingTrack(track: String, location: String) extends AutoCloseable {
    private var done = false
    val trackReader: Iterator[Array[Byte]] = new TrackReader(location = location, music = track).asIterator()

    def streamAudioChunk(): Iterator[Array[Byte]] = {
      trackReader
    }

    override def close(): Unit = {
      done = true
    }

    def isDone: Boolean = done
  }
}
