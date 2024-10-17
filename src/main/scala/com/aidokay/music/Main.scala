package com.aidokay.music

import com.aidokay.music.tracks.MusicProviders
import zio.*
import zio.http.{Server, ServerConfig}

object Main extends ZIOAppDefault {
  private val port = 8088

  val jokeBoxHandler = new JokeBoxHandler(MusicProviders.mp3Provider("V:\\MusicPhotos\\music\\"))

  val routes = new StreamingRoutes(jokeBoxHandler)

  val config1: ServerConfig = ServerConfig.default.port(port)
  val config2: ServerConfig = ServerConfig.default.port(8089)

  val run: ZIO[Any, Throwable, Nothing] = {
    Server.serve[Scope](routes.adminRoutes++routes.listenRoutes).provide(ServerConfig.live(config1), Server.live, Scope.default)
    //Server.serve(routes.listenRoutes).provide(ServerConfig.live(config2), Server.live)
  }
}
