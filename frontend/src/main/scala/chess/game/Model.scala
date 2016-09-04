package chess.game

import chess.shared._

sealed trait Model

object Model {

  case class Normal(board: Board, selectedPiece: Option[Piece], currentPlayer: Player) extends Model

  case class PawnPromoteDialog(move: Promote, previous: Normal) extends Model

}


