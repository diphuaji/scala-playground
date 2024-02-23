import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, BufferOverflowException}
import akka.http.scaladsl.server.Directives._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Route.seal
import akka.http.scaladsl.unmarshalling.Unmarshaller
import scala.io.StdIn
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.Flow
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.model.ws.WebSocketRequest
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import scala.concurrent.duration._

object DuplexExample extends App {
  Server.run
}


object Server {
  def run: Unit = {
    implicit val system = ActorSystem("server-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val echoService: Flow[Message, Message, Any] =
      Flow[Message].map {
        case TextMessage.Strict(text) => {
          println("Server processing message...")
          TextMessage(s"Server received: $text")
        }
        case _ => TextMessage("Message type unsupported")
      }

    val websocketRoute =
      path("websocket") {
        get {
          handleWebSocketMessages(echoService)
        }
      }

    val bindingFuture = Http().bindAndHandle(websocketRoute, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    scala.io.StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}


object Client {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("client-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val messageSource = Source.tick(1.second, 1.second, TextMessage("Hello from client"))
    // val sink = Sink.fold(0)((accu, msg: TextMessage)=> accu+msg.getStrictText.size)
    // val sink = Sink.foreach(println)

    val websocketFlow =
      Flow.fromSinkAndSourceMat(Sink.fold(0)((accu, msg: Message)=> {
        println(s"Got message ")
        accu+msg.asTextMessage.getStrictText.size
      }), messageSource)(Keep.left)

    val (upgradeResponse, closed) =
      Http().singleWebSocketRequest(
        WebSocketRequest("ws://localhost:8080/websocket"),
        clientFlow = websocketFlow
      )

    upgradeResponse.onComplete { upgrade =>
      if (upgrade.isSuccess) {
        println("Client connected")
      } else {
        println("Connection failed")
      }
    }

    closed.onComplete(finalAccu => {
      println(s"Finally accu: ${finalAccu}")
      system.terminate()
    })
  }
}
