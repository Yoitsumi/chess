package chess.shared

/**
 * Created by Kamil on 28.08.2016.
 */
case class Board(pieces: Set[Piece], unmovedPieces: Set[Piece]) {

  def apply(tile: Tile): Option[Piece] =
    pieces.find(_.position == tile)

  def apply(x: Int, y: Int): Option[Piece] =
    this(Tile(x, y))

  def king(player: Player): Piece =
    pieces.find(p => p.pieceKind == King && p.player == player) getOrElse sys.error(s"No $player king on the board!")

  def moved(move: Move): Option[Board] = move match {
    case NormalMove(piece, _) if !pieces(piece) => None
    case Promote(piece, _, _) if !pieces(piece) => None
    case NormalMove(piece, t) =>
      val capturedPiece = this(t)
      Some(copy(
        pieces = (pieces - piece -- capturedPiece) + piece.copy(position = t),
        unmovedPieces = unmovedPieces - piece
      ))

    case Promote(piece, newKind, t) =>
      Some(copy(
        pieces = (pieces - piece -- apply(t)) + piece.copy(
          pieceKind = newKind,
          position = t
        )
      ))

    case LongCastling(player) =>
      for {
        rook <- this(player.farRookPosition)
        k = king(player)
      } yield this.copy(
          pieces = pieces - k - rook +
            k.copy(position = Tile(k.position.x - 2, player.firstRank)) +
            rook.copy(position = Tile(k.position.x - 1, player.firstRank)),
          unmovedPieces = unmovedPieces - k - rook
        )
    case ShortCastling(player) =>
      for {
        rook <- this(player.nearRookPosition)
        k = king(player)
      } yield this.copy(
        pieces = pieces - k - rook +
          k.copy(position = Tile(k.position.x + 2, player.firstRank)) +
          rook.copy(position = Tile(k.position.x + 1, player.firstRank)),
        unmovedPieces = unmovedPieces - k - rook
      )
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
    Board(pawns ++ figures, pawns ++ figures)
  }

}
