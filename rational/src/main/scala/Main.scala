import number.Rational

import scala.language.implicitConversions

object Main extends App {
  implicit def intToRational(x: Int): Rational = new Rational(x)

  println("Hello, Program started!")
  val r1 = new Rational(1, 2)
  val r2 = new Rational(3, 2)
  println(r1 + r2)
  println(r1 * r2)
  println(r1 * 10)
  println(r1 + 5)
  println(5 + r1)
  println(2 * r1)
}
