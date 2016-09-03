package chess.shared

/**
 * Created by Kamil on 28.08.2016.
 */
case class Tile(x: Int, y: Int) {

  override def toString: String =
    ('A' + x).toChar.toString + (y + 1)

  def dark: Boolean = ((x + y) % 2) == 0

  def onBoard: Boolean = x >= 0 && x <= 7 && y >= 0 && y <= 7

}
