package com.aidokay.music

object JokeBox {

  sealed trait JokeBoxState

  case object Playing extends JokeBoxState

  case object Paused extends JokeBoxState

  sealed trait MusicBox

  final case class ListMusic(client: String) extends MusicBox

  final case class PlayMusic(client: String) extends MusicBox

  final case class PauseMusic(client: String) extends MusicBox

  final case class ScheduleMusic(tracks: List[String]) extends MusicBox

  final case class ListedMusic(music: List[String]) extends MusicBox

  case object Ignore extends MusicBox

  case object Cancel extends MusicBox

  final case class SubscribeMusic[T](listener: T) extends MusicBox

  final case class StartPlayMusic[T](listener: T) extends MusicBox
}