package chess.framework

/**
 * Created by Kamil on 14.08.2016.
 */
case class Vector2(x: Double, y: Double) {

  def lengthSq: Double = x*x + y*y

  def length: Double = Math.sqrt(lengthSq)

  def +(b: Vector2): Vector2 = Vector2(x + b.x, y + b.y)
  def -(b: Vector2): Vector2 = Vector2(x - b.x, y - b.y)
  def *(k: Double): Vector2 = Vector2(x * k, y * k)
  def /(k: Double): Vector2 = Vector2(x / k, y / k)

  def cross(b: Vector2): Double = x*b.y - y*b.x
  def dot(b: Vector2): Double = x*b.x + y*b.y

  def *(b: Vector2): Vector2 = Vector2(x * b.x, y * b.y)

  def normalized: Vector2 = this / length


}

object Vector2 {

  val zero: Vector2 = new Vector2(0, 0)

  def angleBetween(a: Vector2, b: Vector2): Double =
    Math.acos(a.normalized dot b.normalized)

}
