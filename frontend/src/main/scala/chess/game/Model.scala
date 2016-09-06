package chess.game

import chess.framework.WebSocketConnection
import chess.shared._

sealed trait Model

object Model {

  case class Normal(board: Board,
                    selectedPiece: Option[Piece],
                    currentPlayer: Player,
                    connection: Option[WebSocketConnection]) extends Model

  case class PawnPromoteDialog(move: Promote, previous: Normal) extends Model

}


