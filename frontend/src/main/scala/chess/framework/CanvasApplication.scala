package chess.framework

/**
 * Created by Kamil on 14.08.2016.
 */
trait CanvasApplication {

  type Model

  type Message


  def initModel: Model

  def update(model: Model, message: CanvasMessage[Message]): Model

  def view(model: Model): Shape

}
