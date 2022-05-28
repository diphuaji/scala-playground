import java.util.Calendar
import scala.io.Source

object Main extends App {

  def printStuffFunc(stuff: => Long) = {
    () => println(stuff)
  }

  def printStuffFunc2(stuff: Long) = {
    () => println(stuff)
  }


//  println("Hello, Program started!")
//  if (args.length > 0) {
//    val source = Source.fromFile(args.head)
//    try {
//      val lines = source.getLines.toList
//
//      val longestWidthLength = lines.reduceLeft((a: String, b: String) => {
//        if (a.length > b.length) a else b
//      }).length.toString.length
//      for (line <- lines) {
//        val length = line.length.toString.length
//        " " * longestWidthLength
//        println(" " * (longestWidthLength - length) + line.length.toString + " | " + line)
//      }
//    } finally {
//      source.close()
//    }
//  } else
//    Console.err.println("Please enter filename")

  val func = printStuffFunc{Calendar.getInstance.getTimeInMillis}
  func()
  Thread.sleep(1000)
  func()

  Thread.sleep(1000)

  val func2 = printStuffFunc2{Calendar.getInstance.getTimeInMillis}
  func2()
  Thread.sleep(1000)
  func2()
}