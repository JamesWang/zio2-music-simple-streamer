package com.aidokay.music

import com.aidokay.music.JokeBox.{MusicBox, StartPlayMusic, SubscribeMusic}
import com.aidokay.music.tracks.MusicProviders
import io.netty.buffer.ByteBuf
import zio.http.*
import zio.http.model.{Headers, Method, Status}
import zio.stream.{ZSink, ZStream}
import zio.direct.defer
import zio.direct.run
import zio.{
  Chunk,
  Clock,
  Duration,
  Hub,
  Schedule,
  Scope,
  Task,
  ZIO,
  ZLayer,
  durationInt,
  http
}

import java.nio.ByteBuffer
import java.util.concurrent.ArrayBlockingQueue

class StreamingRoutes(jokeBoxHandler: JokeBoxHandler) {
  private val hubZio: ZIO[Scope, Response, Hub[Array[Byte]]] = {
    val re = for {
      hub <- Hub.bounded[Array[Byte]](4)
      sink <- ZIO.succeed(ZSink.fromHub(hub))
      _ <- ZStream
        .fromIterator(jokeBoxHandler.streamAudioChunk)
        // .map(ab => Chunk.fromByteBuffer(ByteBuffer.wrap(ab)))
        .schedule(Schedule.fixed(200.milli))
        .run(sink)
        .fork
    } yield hub
    re
  }

  val adminRoutes: http.App[Scope] = Http.collect[Request] {
    case Method.GET -> !! / admin / "list" =>
      Response.text(jokeBoxHandler.list().mkString("\n"))
    case Method.GET -> !! / admin / "schedule" / music =>
      jokeBoxHandler.schedule(music)
      Response.ok
    case Method.GET -> !! / admin / "play" =>
      jokeBoxHandler.play()
      Response.ok
    case Method.GET -> !! / admin / "pause" =>
      jokeBoxHandler.pause()
      Response.ok
  }

  val listenRoutes: http.App[Scope] =
    Http.collectZIO[Request] { case Method.GET -> !! / "listen" =>
      for {
        hub <- hubZio
      } yield {
        println(s"Streaming with hub")
        val stream = ZStream
          .fromHub[Array[Byte]](hub)
          .buffer(4096 * 4)
          .flatMap(ab => ZStream.fromIterator(ab.iterator))
        Response(
          status = Status.Ok,
          headers = Headers.contentType("audio/mpeg"),
          body = Body.fromStream(stream)
        )
      }
    }
}

object StreamingRoutes {
  val live: ZLayer[JokeBoxHandler, Nothing, StreamingRoutes] =
    ZLayer.fromZIO:
      defer:
        StreamingRoutes(ZIO.service[JokeBoxHandler].run)
}
