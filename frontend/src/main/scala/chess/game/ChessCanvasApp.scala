package chess.game

import chess.framework._
import chess.game.Model.PawnPromoteDialog
import chess.shared._

/**
 * Created by Kamil on 28.08.2016.
 */
object ChessCanvasApp extends CanvasApplication {

  override type Model = chess.game.Model

  override type Message = Nothing

  val TileSize = 51
  val DarkTileColor = "#855624"
  val LightTileColor = "#EBD765"

  val SpriteSheetFileName = "Chess_Pieces_Sprite.svg"
  val SpriteSize = Vector2(45, 45)
  val SpriteMargin = (Vector2(TileSize, TileSize) - SpriteSize) / 2

  val SpriteSheetXOrder = Seq(King, Queen, Bishop, Knight, Rook, Pawn)
  val SpriteSheetYOrder = Seq(White, Black)

  override def initModel: Model = Model.Normal(
    board = Board.initial,
    selectedPiece = None,
    currentPlayer = White
  )

  override def update(model: Model, message: CanvasMessage[Nothing]): Model = (message, model) match {
    case (MouseDown(p, 0), m: Model.Normal) =>
      val clickedTile = Tile((p.x / TileSize).toInt, (p.y / TileSize).toInt)
      m match {
        case Model.Normal(_, Some(Piece(_, _, t)), _)
          if t == clickedTile =>

          m.copy(selectedPiece = None)

        case Model.Normal(board, Some(piece), player) =>
          val move = Ruleset.mapMove(board, piece, clickedTile)
          println(s"move mapped to: $move")
          move match {
            case move: Promote =>
              if (Ruleset.isMoveAllowed(board, move))
                PawnPromoteDialog(move, m)
              else m

            case _ =>
              val model_? = for {
                _ <- Some().filter(_ => Ruleset.isMoveAllowed(board, move))
                newBoard <- board.moved(move)
              } yield m.copy(board = newBoard, selectedPiece = None, currentPlayer = player.opponent)
              model_? getOrElse model
          }

        case Model.Normal(board, None, player) =>
          val model_? = for {
            piece <- board(clickedTile)
            if piece.player == player
          } yield m.copy(selectedPiece = Some(piece))
          model_? getOrElse model

      }

    case (MouseDown(p, 0), Model.PawnPromoteDialog(promote, Model.Normal(board, _, player))) =>
      val clickedTile = Tile((p.x / TileSize).toInt, (p.y / TileSize).toInt)
      val model_? = for {
        newKind <- promoteDialogOrder.lift(clickedTile.y - 1)
        if clickedTile.x == 8
        newBoard <- board.moved(promote.copy(to = newKind))
      } yield Model.Normal(newBoard, None, player.opponent)
      model_? getOrElse model

    case _ => model
  }

  override def view(model: Model): Shape = model match {
    case m: Model.Normal => viewNormal(m)
    case m @ Model.PawnPromoteDialog(_, normal) =>
      Compose(Seq(viewNormal(normal), viewPawnPromoteDialog(m)))
  }

  def viewNormal(model: Model.Normal): Shape = {
    val tiles = for {
      x <- 0 to 7
      y <- 0 to 7
      tile = Tile(x, y)
      color = /*if(Ruleset.isAttacked(model.board, tile, model.currentPlayer.opponent)) "red"
              else */if(Tile(x, y).dark) DarkTileColor
              else LightTileColor
    } yield Fill(color, Rectangle(Vector2(x, y) * TileSize, Vector2(TileSize, TileSize)))
    val pieces = for {
      Piece(p, k, t) <- model.board.pieces
    } yield {
        val sx = SpriteSheetXOrder.indexOf(k)
        val sy = SpriteSheetYOrder.indexOf(p)
        Image(SpriteSheetFileName, tileStart(t) + SpriteMargin, SpriteSize, Vector2(sx, sy) * SpriteSize)
      }
    val selectionCircle = model.selectedPiece.map {
      case Piece(_, _, t) =>
        Stroke("black", Circle(tileCenter(t), TileSize / 2))
    }
    val availableMoves = for {
      piece <- model.selectedPiece.toSeq
      t <- Ruleset.genAllowedMoves(model.board, piece)
    } yield Stroke("blue", Circle(tileCenter(t), TileSize / 2))

    val statusCircle = {
      val color =
        if(Ruleset.checkMate(model.board, model.currentPlayer)) "red"
        else if(Ruleset.check(model.board, model.currentPlayer)) "orange"
        else "green"
      Fill(color, Circle(Vector2(8 * TileSize + 10, 10), 10))
    }

    Compose(tiles ++ pieces ++ selectionCircle ++ availableMoves ++ Seq(statusCircle))
  }

  val promoteDialogOrder = Seq(Queen, Rook, Knight, Bishop)

  def viewPawnPromoteDialog(m: Model.PawnPromoteDialog): Shape = {
    val buttons = for {
      (k, y) <- promoteDialogOrder.zipWithIndex
    } yield viewPiece(Piece(m.move.piece.player, k, Tile(8, y + 1)))
    Compose(buttons)
  }


  def viewPiece(p: Piece): Shape = {
    val sx = SpriteSheetXOrder.indexOf(p.pieceKind)
    val sy = SpriteSheetYOrder.indexOf(p.player)
    Image(SpriteSheetFileName, tileStart(p.position) + SpriteMargin, SpriteSize, Vector2(sx, sy) * SpriteSize)
  }


  def tileCenter(t: Tile): Vector2 =
    Vector2(t.x + 0.5, t.y + 0.5) * TileSize

  def tileStart(t: Tile): Vector2 =
    Vector2(t.x, t.y) * TileSize

}
