package chess.game

import chess.framework._
import chess.shared._

/**
 * Created by Kamil on 28.08.2016.
 */
object ChessCanvasApp extends CanvasApplication {

  case class M(board: Board, selectedPiece: Option[Piece], currentPlayer: Player)

  override type Model = M

  override type Message = Nothing

  val TileSize = 51
  val DarkTileColor = "#855624"
  val LightTileColor = "#EBD765"

  val SpriteSheetFileName = "Chess_Pieces_Sprite.svg"
  val SpriteSize = Vector2(45, 45)
  val SpriteMargin = (Vector2(TileSize, TileSize) - SpriteSize) / 2

  val SpriteSheetXOrder = Seq(King, Queen, Bishop, Knight, Rook, Pawn)
  val SpriteSheetYOrder = Seq(White, Black)

  override def initModel: ChessCanvasApp.Model = M(
    board = Board.initial,
    selectedPiece = None,
    currentPlayer = White
  )

  override def update(model: ChessCanvasApp.Model, message: CanvasMessage[Nothing]): M = message match {
    case MouseDown(p, 0) =>
      val clickedTile = Tile((p.x / TileSize).toInt, (p.y / TileSize).toInt)
      model match {
        case M(_, Some(Piece(_, _, t)), _)
          if t == clickedTile =>

          model.copy(selectedPiece = None)

        case M(board, Some(piece), player) =>
          val move = Ruleset.mapMove(board, piece, clickedTile)
          val model_? = for {
            _ <- Some().filter(_ => Ruleset.isMoveAllowed(board, move))
            newBoard <- board.moved(move)
          } yield model.copy(board = newBoard, selectedPiece = None, currentPlayer = player.opponent)
          model_? getOrElse model

        case M(board, None, player) =>
          val model_? = for {
            piece <- board(clickedTile)
            if piece.player == player
          } yield model.copy(selectedPiece = Some(piece))
          model_? getOrElse model

      }
    case _ => model
  }

  override def view(model: ChessCanvasApp.Model): Shape = {
    val tiles = for {
      x <- 0 to 7
      y <- 0 to 7
      tile = Tile(x, y)
      color = /*if(Ruleset.isAttacked(model.board, tile, model.currentPlayer.opponent)) "red"
              else */if(Tile(x, y).dark) DarkTileColor
              else LightTileColor
    } yield Fill(color, Rectangle(Vector2(x, y) * TileSize, Vector2(TileSize, TileSize)))
    val pieces = for {
      Piece(p, k, Tile(x, y)) <- model.board.pieces
    } yield {
        val sx = SpriteSheetXOrder.indexOf(k)
        val sy = SpriteSheetYOrder.indexOf(p)
        Image(SpriteSheetFileName, Vector2(x, y) * TileSize + SpriteMargin, SpriteSize, Vector2(sx, sy) * SpriteSize)
      }
    val selectionCircle = model.selectedPiece.map {
      case Piece(_, _, Tile(x, y)) =>
        Stroke("black", Circle(Vector2(x + 0.5, y + 0.5) * TileSize, TileSize / 2))
    }
    val availableMoves = for {
      piece <- model.selectedPiece.toSeq
      Tile(x, y) <- Ruleset.genAllowedMoves(model.board, piece)
    } yield Stroke("blue", Circle(Vector2(x + 0.5, y + 0.5) * TileSize, TileSize / 2))

    val statusCircle = {
      val color =
        if(Ruleset.checkMate(model.board, model.currentPlayer)) "red"
        else if(Ruleset.check(model.board, model.currentPlayer)) "orange"
        else "green"
      Fill(color, Circle(Vector2(8 * TileSize + 10, 10), 10))
    }

    Compose((tiles ++ pieces ++ selectionCircle ++ availableMoves ++ Seq(statusCircle)): _*)
  }



}
