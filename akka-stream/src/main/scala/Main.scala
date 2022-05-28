import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream._
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("QuickStart")
  def basic = {
    //  val source = Source(1 to 10)
    //  val flow = Flow[Int].map(_*2).mapMaterializedValue(_=>"what")
    //
    //  val graph = source.viaMat(flow)(Keep.right).toMat(Sink.seq)(Keep.both)
    //  val result = graph.run()
    //  println(result._1)

    // Starting from a Sink
    println("begin")
    val flow = Flow[Int].map{i=>
      println("computing...")
      Thread.sleep(2000)
      i*2
    }
    val sink = flow.toMat(Sink.foreach { i =>
      println(i)
    })(Keep.right)
    val r= Source(1 to 6).toMat(sink)(Keep.right).run()
    println(r)
  }


  /**
   * This
   */
  def graph = {
    val sink = Sink.seq[(String, Int)]
    val g = RunnableGraph.fromGraph(GraphDSL.create(sink) { implicit builder => s =>
      import GraphDSL.Implicits._
      val in = Source(1 to 10).map(i=>(i.toString, 1L))
      val partition = builder.add(Partition[(String, Long)](2, a=>{
        if (a._1.toInt<6) 0 else 1
      }))
      in ~> partition.in
      val merge = builder.add(Merge[(String, Long)](2))
      partition.out(1) ~> merge.in(1)
      partition.out(0) ~> merge.in(0)
      merge.out~> s
      ClosedShape
    })
     g.run()
  }

  graph.map(a=> print(a))(system.dispatcher)

  system.terminate()
}
