package chess.framework

/**
 * Created by Kamil on 05.09.2016.
 */
sealed trait Request[+M]

object WS {

  case class Open[M](uri: String,
                     onMessage: (WebSocketConnection, String) => M,
                     onOpened: Option[WebSocketConnection => M] = None) extends Request[M]

  case class Send[M](connection: WebSocketConnection) extends Request[Nothing]

}
