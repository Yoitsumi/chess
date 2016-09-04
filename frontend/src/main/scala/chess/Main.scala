package chess

import chess.framework._
import org.scalajs.dom.raw.{HTMLImageElement, HTMLElement, HTMLDivElement, HTMLCanvasElement}

import scala.collection.mutable
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

/**
 * Created by Kamil on 14.08.2016.
 */
object Main extends JSApp {

  val canvas = document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]
  val context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  val imgContainer = {
    val e = document.createElement("div").asInstanceOf[HTMLDivElement]
    e.style.visibility = "none"
    e
  }

  val imgCache: mutable.Map[String, HTMLElement] = mutable.Map.empty

  val app = chess.game.ChessCanvasApp

  var model: app.Model = app.initModel

  println(s"initial model: $model")

  @JSExport
  override def main(): Unit = {
    draw(app.view(model), DrawParams())
    canvas.addEventListener("mousemove", (e: MouseEvent) => {
      val bounds = canvas.getBoundingClientRect()
      update(MouseMove(Vector2(e.clientX - bounds.left, e.clientY - bounds.top)))
    })
    canvas.addEventListener("mousedown", (e: MouseEvent) => {
      val bounds = canvas.getBoundingClientRect()
      update(MouseDown(Vector2(e.clientX - bounds.left, e.clientY - bounds.top), e.button))
    })
  }

  def update(msg: CanvasMessage[app.Message]): Unit = {
    val oldModel = model
    model = app.update(model, msg)
    if(model != oldModel) {
      console.log("Redrawing...")
      clean()
      draw(app.view(model), DrawParams())
    }
  }

  def clean(): Unit = {
    context.clearRect(0, 0, canvas.width, canvas.height)
  }

  case class DrawParams(fill: Boolean = false, stroke: Boolean = false)

  def draw(shape: Shape, params: DrawParams): Unit = shape match {
    case Compose(s) => s.foreach(draw(_, params))
    case Fill(c, s) =>
      context.save()
      context.fillStyle = c
      draw(s, params.copy(fill = true))
      context.restore()
    case Stroke(c, s) =>
      context.save()
      context.strokeStyle = c
      draw(s, params.copy(stroke = true))
      context.restore()
    case Circle(c, r) =>
      path(params) {
        context.arc(c.x, c.y, r, 0, Math.PI*2)
      }
    case Polygon(first, rest @ _*) => path(params) {
      context.moveTo(first.x, first.y)
      rest.foreach(v => context.lineTo(v.x, v.y))
      context.closePath()
    }
    case Line(a, b) => path(params) {
      context.moveTo(a.x, a.y)
      context.lineTo(b.x, b.y)
    }
    case Rectangle(c, s) =>
      if(params.fill) {
        context.fillRect(c.x, c.y, s.x, s.y)
      }
      if(params.stroke) {
        context.strokeRect(c.x, c.y, s.x, s.y)
      }
    case Image(url, p, s, o) =>
      val img = imgCache.getOrElseUpdate(url, {
        val e = document.createElement("img").asInstanceOf[HTMLImageElement]
        e.src = url
        e
      })
      context.drawImage(img, o.x, o.y, s.x, s.y, p.x, p.y, s.x, s.y)
  }

  def path(params: DrawParams)(body: =>Unit): Unit = {
    context.beginPath()
    body
    if(params.fill) {
      context.fill()
    }
    if(params.stroke) {
      context.stroke()
    }
  }

}
