package chess.framework

/**
 * Created by Kamil on 14.08.2016.
 */
sealed trait CanvasMessage[+M]

case class MouseMove(newPosition: Vector2) extends CanvasMessage[Nothing]

case class MouseDown(position: Vector2, button: Int) extends CanvasMessage[Nothing]

case class CustomMessage[M](message: M) extends CanvasMessage[M]
