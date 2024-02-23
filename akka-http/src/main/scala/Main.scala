import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, BufferOverflowException}
import akka.http.scaladsl.server.Directives._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Route.seal
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import scala.io.StdIn
import akka.stream.scaladsl.Sink
import akka.http.scaladsl.util.FastFuture


object Main extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  println(Http().sslConfig.config)

  startServer


  def startServer = {
    

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }

    val bindingFuture = Http().bindAndHandle(Route.handlerFlow(route), "0.0.0.0", 8080)

    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    val b = StdIn.readLine() // let it run until user presses return
    println(s"b: $b")

    bindingFuture
      .flatMap(sb=>{
        println("enter pressed")
        sb.unbind()
      }) // trigger unbinding from the port
      .onComplete(_ => {
        println("terminating system")
        system.terminate()
        println("system terminated")
      }) // and shutdown when done
  }
}