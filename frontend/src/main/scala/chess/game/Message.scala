package chess.game

import chess.framework.WebSocketConnection

/**
 * Created by Kamil on 05.09.2016.
 */
sealed trait Message

object Message {

  case class WebSocketOpened(connection: WebSocketConnection) extends Message

  case class WebSocketMessageReceived(message: String) extends Message

}
