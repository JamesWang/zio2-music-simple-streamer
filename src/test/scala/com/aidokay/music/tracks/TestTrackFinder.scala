package com.aidokay.music.tracks

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TestTrackFinder
    extends AnyWordSpec
    with Matchers
    with TypeCheckedTripleEquals {

  "TrackFileLoader.load()" should {
    "get a list of mp3 files" in {
      val result = TracksFinder.map3FileFinder.load("V:\\MusicPhotos\\music")
      result.size should ===(53)
    }
  }
}
