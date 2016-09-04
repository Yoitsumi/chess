package chess.shared

/**
 * Created by Kamil on 29.08.2016.
 */
object Ruleset {

//  def mapMove(board: Board, piece: Piece, to: Tile): Option[Move] = piece match {
//    case Piece(player, Pawn, from)
//      if (from.x - to.x).abs == 1
//      && (to.y - from.y) == player.forwardDirection
//      && board(to).exists(_.player == player.opponent) =>
//
//      Some(NormalMove(player, from, to))
//
//    case Piece(player, Pawn, from)
//      if from.x == to.x
//      && board(to).isDefined =>
//
//      None
//
//    case Piece(player, Pawn, from)
//      if from.x == to.x
//      && (to.y - from.y) == player.forwardDirection =>
//
//      Some(NormalMove(player, from, to))
//
//    case Piece(player, Pawn, from)
//      if from.x == to.x
//      && from.y == player.pawnRank
//      && to.y == player.pawnRank + player.forwardDirection*2 =>
//
//      Some(NormalMove(player, from, to))
//
//    case Piece(_, Pawn, _) => None
//
//    case _ => None
//  }

  def mapMove(board: Board, piece: Piece, to: Tile): Move = (piece, to) match {
    case (Piece(player, Pawn, _), Tile(_, y))
      if y == player.lastRank =>

      Promote(piece, Pawn, to) // The target type will be chosen later

    case (Piece(player, Pawn, from), _)
      if to.y == player.opponent.pawnRank + player.opponent.forwardDirection
      && (from.x - to.x).abs == 1
      && board(to).isEmpty =>

      EnPassant(piece, to)

    case (Piece(player, King, Tile(4, _)), Tile(2, _)) => LongCastling(player)
    case (Piece(player, King, Tile(4, _)), Tile(6, _)) => ShortCastling(player)

    case _ => NormalMove(piece, to)
  }

  private def genMoves(init: Tile)(incr: (Int, Int)): Seq[Tile] =
    Stream.iterate(init)(t => Tile(t.x + incr._1, t.y + incr._2)).takeWhile(_.onBoard).tail

  private def genRookMoves(pos: Tile): Seq[Tile] =
    genMoves(pos)(0, 1) ++ genMoves(pos)(0, -1) ++ genMoves(pos)(1, 0) ++ genMoves(pos)(-1, 0)

  private def genBishopMoves(pos: Tile): Seq[Tile] =
    genMoves(pos)(1, 1) ++ genMoves(pos)(1, -1) ++ genMoves(pos)(-1, 1) ++ genMoves(pos)(-1, -1)

  private def genQueenMoves(pos: Tile): Seq[Tile] =
    genRookMoves(pos) ++ genBishopMoves(pos)

  private def genKingMoves(pos: Tile): Seq[Tile] = {
    val normal = for {
      x <- -1 to 1
      y <- -1 to 1
      if x != 0 || y != 0
      tile = Tile(pos.x + x, pos.y + y)
      if tile.onBoard
    } yield tile
    Tile(2, pos.y) +: Tile(6, pos.y) +: normal
  }

  private def genKnightMoves(pos: Tile): Seq[Tile] = for {
    (dx, dy) <- Seq((1, 2), (2, 1))
    x <- Seq(dx, -dx)
    y <- Seq(dy, -dy)
    tile = Tile(pos.x + x, pos.y + y)
    if tile.onBoard
  } yield tile

  private def obstructed(from: Tile, to: Tile, board: Board): Boolean = {
    val dx = (to.x - from.x).signum
    val dy = (to.y - from.y).signum
    genMoves(from)(dx, dy).takeWhile(_ != to).exists(board(_).isDefined)
  }

  def isMoveLegal(board: Board, move: Move): Boolean = move match {
    case NormalMove(_, to)
      if ! to.onBoard => false

    case NormalMove(Piece(player, Pawn, from), to)
      if (from.x - to.x).abs == 1
      && (to.y - from.y) == player.forwardDirection
      && board(to).exists(_.player == player.opponent) => true

    case NormalMove(Piece(player, Pawn, from), to)
      if from.x == to.x
      && board(to).isDefined => false

    case NormalMove(Piece(player, Pawn, from), to)
      if from.x == to.x
      && (to.y - from.y) == player.forwardDirection => true

    case NormalMove(Piece(player, Pawn, from), to)
      if from.x == to.x
      && from.y == player.pawnRank
      && to.y == player.pawnRank + player.forwardDirection*2
      && board(from.x, from.y + player.forwardDirection).isEmpty => true


    case NormalMove(Piece(_, Bishop, from), to)
      if ! genBishopMoves(from).contains(to) => false

    case NormalMove(Piece(_, Rook, from), to)
      if ! genRookMoves(from).contains(to) => false

    case NormalMove(Piece(_, Queen, from), to)
      if ! genQueenMoves(from).contains(to) => false

    case NormalMove(Piece(player, Bishop | Rook | Queen, from), to)
      if ! obstructed(from, to, board) =>

      board(to).forall(_.player == player.opponent) // true if empty or occupied by opponent's piece

    case NormalMove(Piece(player, Knight, from), to) =>
      val dx = (from.x - to.x).abs
      val dy = (from.y - to.y).abs
      Set((2, 1), (1, 2))(dx, dy) && board(to).forall(_.player == player.opponent)

    case NormalMove(Piece(player, King, from), to) =>
      val dx = (from.x - to.x).abs
      val dy = (from.y - to.y).abs
      dx <= 1 && dy <= 1 && board(to).forall(_.player == player.opponent)



    case Promote(Piece(player, Pawn, from), _, to)
      if (to.x - from.x).abs == 1 =>

      board(to).exists(_.player == player.opponent)

    case Promote(Piece(player, Pawn, from), _, to)
      if to.x == from.x =>

      board(to).isEmpty


    case ShortCastling(player) =>
      val ? = for {
        rook <- board(player.nearRookPosition)
        king = board.king(player)
      } yield castlingLegal(board, king, rook)
      ? getOrElse false

    case LongCastling(player) =>
      val ? = for {
        rook <- board(player.farRookPosition)
        king = board.king(player)
      } yield castlingLegal(board, king, rook)
      ? getOrElse false

    case _ => false

  }

  private def castlingLegal(board: Board, king: Piece, rook: Piece): Boolean = {
    val unmoved = board.unmovedPieces(rook) && board.unmovedPieces(king)
    val dx = (rook.position.x - king.position.x).signum
    assert(dx != 0, "Rook and king can't be on the same file during castling!")
    val tilesBetween = Stream.iterate(king.position){
      case Tile(x, y) =>
        Tile(x + dx, y)
    } takeWhile {
      case Tile(x, _) =>
        x != rook.position.x
    } take 8 tail // Will never be more than 8, ant this will make sure we don't get an infinite stream here

    val notObstructed = tilesBetween.forall(board(_).isEmpty)
    val notAttacked = (for {
      x <- 0 to 2 map (_ * dx + king.position.x)
    } yield isAttacked(board, Tile(x, king.position.y), king.player.opponent)).forall(!_)

    unmoved && notObstructed && notAttacked
  }

  def isMoveAllowed(board: Board, move: Move): Boolean = {
    isMoveLegal(board, move) && !board.moved(move).exists(check(_, move.player))
  }

  def isAttacked(board: Board, tile: Tile, by: Player): Boolean =
    board.pieces.filter(_.player == by).exists(p => isMoveLegal(board, NormalMove(p, tile)))

  def check(board: Board, player: Player): Boolean =
    isAttacked(board, board.king(player).position, player.opponent)

  def checkMate(board: Board, player: Player): Boolean =
    check(board, player) && hasNoMoves(board, player)

  def hasNoMoves(board: Board, player: Player): Boolean =
    board.pieces
      .filter(_.player == player)
      .forall(genAllowedMoves(board, _).isEmpty)

  def genAllowedMoves(board: Board, piece: Piece): Set[Tile] = (piece match {
    case Piece(player, Pawn, from) =>
      Seq( from.copy(y = from.y + player.forwardDirection)
         , from.copy(y = from.y + player.forwardDirection * 2)
         , Tile(from.x - 1, from.y + player.forwardDirection)
         , Tile(from.x + 1, from.y + player.forwardDirection)
         )

    case Piece(player, Bishop, from) =>
      genBishopMoves(from)

    case Piece(player, Rook, from) =>
      genRookMoves(from)

    case Piece(player, Queen, from) =>
      genQueenMoves(from)

    case Piece(player, King, from) =>
      genKingMoves(from)

    case Piece(player, Knight, from) =>
      genKnightMoves(from)

  }).filter(t => isMoveAllowed(board, mapMove(board, piece, t))).toSet


}
