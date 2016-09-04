package chess.shared

/**
 * Created by Kamil on 28.08.2016.
 */
sealed abstract class Player {

  def forwardDirection = this match {
    case White => 1
    case Black => -1
  }

  def firstRank: Int = this match {
    case White => 0
    case Black => 7
  }

  def lastRank: Int = 7 - firstRank

  def pawnRank: Int = this match {
    case White => 1
    case Black => 6
  }

  def opponent: Player = this match {
    case White => Black
    case Black => White
  }


  def farRookPosition: Tile =
    Tile(0, firstRank)

  def nearRookPosition: Tile =
    Tile(7, firstRank)

}

case object White extends Player
case object Black extends Player
