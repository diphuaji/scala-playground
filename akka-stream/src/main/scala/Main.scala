import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.stream._
import akka.util.ByteString

import scala.concurrent._
import scala.concurrent.duration._
import java.nio.file.Paths
import akka.stream.SourceShape
import akka.stream.stage.GraphStage
import akka.stream.stage.GraphStageLogic
import akka.stream.stage.OutHandler

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))
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
    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val in = Source(1 to 10).map(i=>(i.toString, 1L))
      val partition = builder.add(Partition[(String, Long)](2, a=>{
        if (a._1.toInt<6) 0 else 1
      }))
      in ~> partition.in
      val merge = builder.add(Merge[(String, Long)](2))
      partition.out(1) ~> merge.in(1)
      partition.out(0) ~> merge.in(0)
      merge ~> Sink.ignore
      ClosedShape
    })
     g.run()
  }


  class NumbersSource extends GraphStage[SourceShape[Int]] {
    // Define the (sole) output port of this stage
    val out: Outlet[Int] = Outlet("NumbersSource")
    // Define the shape of this stage, which is SourceShape with the port we defined above
    override val shape: SourceShape[Int] = SourceShape(out)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
      new GraphStageLogic(shape) {
        // All state MUST be inside the GraphStageLogic,
        // never inside the enclosing GraphStage.
        // This state is safe to access and modify from all the
        // callbacks that are provided by GraphStageLogic and the
        // registered handlers.
        private var counter = 1

        setHandler(out, new OutHandler {
          override def onPull(): Unit = {
            push(out, counter)
            counter += 1
          }
        })
      }
  }

  def testZip = {
    val s1 = Source(Seq(1,2,3))
    val plusFlow = Flow.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._
      val broadcast = b.add(Broadcast[Int](2))
      val zip = b.add(ZipWith((n1: Int, n2: Int)=>n1+n2))
      broadcast.out(0) ~> zip.in0
      broadcast.out(1).filter(_<2) ~> zip.in1
      FlowShape(broadcast.in, zip.out)
    })
     println(Await.result(s1.via(plusFlow).runWith(Sink.seq).map(println), 10.seconds))
  }

  def testSourceZip = {
    val s1 = Source(Seq(1,2,3))
    val s2 = Source(Seq(1,2,3)).filter(_<2)
    println(Await.result(s1.zip(s2).map({
      case (n1, n2) => n1+n2
    }).runWith(Sink.seq), 10.seconds))
  }

  def testFlowZip = {
    val s1 = Source(Seq(1,2))
    val s2 = Source(Seq(1,2,3))
    val f1 = Flow[Int]
    val f2 = Flow[Int].filter(_<2)
    println(Await.result(s2.via(f2.zip(s1)).runWith(Sink.seq), 10.seconds))
  }

  def testMaterializer = {
    val source = Source(1 to 10)
    val sink = Sink.fold[Int, Int](0)(_ + _)

    // connect the Source to the Sink, obtaining a RunnableGraph
    val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

    // materialize the flow and get the value of the sink
    val sum: Future[Int] = runnable.run()
    println(s"result: ${Await.result( sum, 2.seconds)}")
  }

  testMaterializer

  system.terminate()
}
