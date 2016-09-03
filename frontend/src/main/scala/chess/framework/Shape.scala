package chess.framework

/**
 * Created by Kamil on 14.08.2016.
 */
sealed trait Shape

case class Compose(shapes: Shape*) extends Shape

case class Circle(center: Vector2, radius: Double) extends Shape

case class Polygon(vertices: Vector2*) extends Shape

case class Line(start: Vector2, end: Vector2) extends Shape

case class Fill(color: String, shape: Shape) extends Shape

case class Stroke(color: String, shape: Shape) extends Shape

case class Rectangle(corner: Vector2, size: Vector2) extends Shape

case class Image(url: String, position: Vector2, size: Vector2, offset: Vector2 = Vector2.zero) extends Shape

