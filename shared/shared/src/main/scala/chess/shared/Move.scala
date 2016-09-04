package chess.shared

/**
 * Created by Kamil on 28.08.2016.
 */
sealed trait Move {
  def player: Player
}

final case class NormalMove(piece: Piece, to: Tile) extends Move {
  override def player: Player = piece.player
}

final case class Promote(piece: Piece, to: PieceKind, at: Tile) extends Move {
  override def player: Player = piece.player
}

final case class EnPassant(piece: Piece, to: Tile) extends Move {
  override def player: Player = piece.player
}

final case class LongCastling(player: Player) extends Move
final case class ShortCastling(player: Player) extends Move
