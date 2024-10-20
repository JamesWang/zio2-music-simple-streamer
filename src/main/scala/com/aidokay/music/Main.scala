package com.aidokay.music

import com.aidokay.music.tracks.MusicProviders
import zio.*
import zio.http.{Server, ServerConfig}

object Main extends ZIOAppDefault {
  private val port = 8088

  private val musicApp = for {
    routes <- ZIO.service[StreamingRoutes]
    _      <- Server
                .serve[Scope](routes.adminRoutes ++ routes.listenRoutes)
                .provide(
                    ServerConfig.live(ServerConfig.default.port(port)),
                    Server.live,
                    Scope.default
                ).debug(s"Server is started on port:$port")
  } yield ()

  val run: ZIO[Any, Throwable, Unit] =
    musicApp
      .provide(
        StreamingRoutes.live,
        ZLayer.succeed(MusicConf("V:\\MusicPhotos\\music\\")),
        MusicProviders.mp3,
        JokeBoxHandler.live
      )
}
