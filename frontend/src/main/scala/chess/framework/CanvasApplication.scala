package chess.framework

/**
 * Created by Kamil on 14.08.2016.
 */
trait CanvasApplication {

  type Model

  type Message


  def initModel: Model

  def initRequests: Seq[Request[Message]] = Seq()

  def update(model: Model, message: CanvasMessage[Message]): (Model, Seq[Request[Message]])

  def view(model: Model): Shape

}
