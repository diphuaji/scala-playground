import java.util.Calendar
import scala.io.Source

object Main extends App {

  def printStuffFunc(stuff: => Long) = {
    () => println(stuff)
  }

  def printStuffFunc2(stuff: Long) = {
    () => println(stuff)
  }


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
