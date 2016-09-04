package chess

import chess.shared._
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by Kamil on 05.09.2016.
 */
class RulesetTests extends FlatSpec with Matchers {

  "Castling" should "not be allowed if king has moved" in {
    val king = Piece(White, King, Tile(4, 0))
    val rook = Piece(White, Rook, Tile(7, 0))
    val board = Board(Set(king, rook), Set(rook))
    Ruleset.isMoveAllowed(board, ShortCastling(White)) should be (false)
  }

  it should "not be allowed if the rook has moved" in {
    val king = Piece(White, King, Tile(4, 0))
    val rook = Piece(White, Rook, Tile(7, 0))
    val board = Board(Set(king, rook), Set(king))
    Ruleset.isMoveAllowed(board, ShortCastling(White)) should be (false)
  }

}
