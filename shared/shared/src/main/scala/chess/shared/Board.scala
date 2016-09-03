package chess.shared

/**
 * Created by Kamil on 28.08.2016.
 */
case class Board(pieces: Set[Piece]) {

  def apply(tile: Tile): Option[Piece] =
    pieces.find(_.position == tile)

  def apply(x: Int, y: Int): Option[Piece] =
    this(Tile(x, y))

  def king(player: Player): Piece =
    pieces.find(p => p.pieceKind == King && p.player == player) getOrElse sys.error(s"No $player king on the board!")

  def moved(move: Move): Option[Board] = move match {
    case NormalMove(piece, _) if !pieces(piece) => None
    case Promote(piece, _) if !pieces(piece) => None
    case NormalMove(piece, t) =>
      val capturedPiece = this(t)
      Some(copy(pieces = (pieces - piece -- capturedPiece) + piece.copy(position = t)))

    case Promote(piece, t) =>
      Some(copy(
        pieces = (pieces - piece) + piece.copy(
          pieceKind = t,
          position = piece.position.copy(y = piece.player.lastRank)
        )
      ))
  }

}

object Board {

  def initial: Board = {
    val pawns = for {
      player <- Set(White, Black)
      pawnRank = player.pawnRank
      x <- 0 to 7
    } yield Piece(player, Pawn, Tile(x, pawnRank))
    val figureOrder = Seq(Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook)
    val figures = for {
      player <- Set(White, Black)
      rank = player.firstRank
      (piece, x) <- figureOrder.zipWithIndex
    } yield Piece(player, piece, Tile(x, rank))
    Board(pawns ++ figures)
  }

}
