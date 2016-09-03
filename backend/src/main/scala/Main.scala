package chess

import org.http4s._, org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.staticcontent.{fileService, FileService}
import org.http4s.server.{staticcontent, Server, ServerApp}

import scala.sys
import scalaz.concurrent.Task

/**
 * Created by Kamil on 29.08.2016.
 */
object Main extends ServerApp {

  val service = HttpService {
    case GET -> Root / "test" =>
      Ok("Hello, World!")

    case GET -> Root =>
      PermanentRedirect(uri("/static/index.html"))
  }

  val filesService = fileService(FileService.Config("static"))

  println(sys.env.get("PORT"))

  override def server(args: List[String]): Task[Server] =
    BlazeBuilder
      .bindHttp(sys.env.get("PORT").map(_.toInt) getOrElse 8080, "0.0.0.0")
      .mountService(service, "/")
      .mountService(filesService, "/static")
      .start

}
