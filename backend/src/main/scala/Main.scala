package chess

import org.http4s._, org.http4s.dsl._
import org.http4s.server.websocket.WS
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.staticcontent.{fileService, FileService}
import org.http4s.server.{staticcontent, Server, ServerApp}
import org.http4s.websocket.WebsocketBits.Text

import scala.collection.mutable
import scala.sys
import scalaz.concurrent.Task
import scalaz.stream.{async, Exchange}
import scalaz.stream.async.mutable.{Queue, Signal}

/**
 * Created by Kamil on 29.08.2016.
 */
object Main extends ServerApp {

  val signals: mutable.Map[Int, Signal[String]] = mutable.Map.empty

  val service = HttpService {
    case GET -> Root / "test" =>
      Ok("Hello, World!")

    case GET -> Root / "ws" / "game" / IntVar(id) =>
      val signal = signals.getOrElseUpdate(id, async.signalUnset[String])

      WS(Exchange(
        signal.discrete.map(Text(_)),
        signal.sink.contramap {
          case Text(msg, _) => Signal.Set(msg)
          case _ => Signal.CompareAndSet((_: Option[String]) => None: Option[String])
        }
      ))

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
