package number

import scala.annotation.tailrec
import scala.language.implicitConversions

class Rational(n: Int, d: Int) {
  require(d != 0)

  private val g = gcd(n.abs, d.abs)

  val numer = n / g
  val denom = d / g

  override def toString = s"$numer/$denom"

  def this(n: Int) = this(n, 1)

  def +(that: Rational): Rational =
    new Rational(
      n * that.denom + that.numer * d,
      d * that.denom
    )

  def +(that: Int): Rational =
    new Rational(
      numer + that * denom,
      denom
    )

  def *(that: Rational): Rational =
    new Rational(
      numer * that.numer,
      denom * that.denom
    )

  def *(that: Int): Rational =
    new Rational(
      numer * that,
      denom
    )

  @tailrec
  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}