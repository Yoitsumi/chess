package chess

import org.http4s._, org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.staticcontent.{fileService, FileService}
import org.http4s.server.{staticcontent, Server, ServerApp}

import scalaz.concurrent.Task

/**
 * Created by Kamil on 29.08.2016.
 */
object Main extends ServerApp {

  val service = HttpService {
    case GET -> Root / "test" =>
      Ok("Hello, World!")
  }

  val filesService = fileService(FileService.Config("static"))

  override def server(args: List[String]): Task[Server] =
    BlazeBuilder
      .bindHttp(8080, "localhost")
      .mountService(service, "/")
      .mountService(filesService, "/static")
      .start

}
