package com.aidokay.music.tracks

trait AudioProvider[T] {
  def audioList: List[T]
  val location: String
}
