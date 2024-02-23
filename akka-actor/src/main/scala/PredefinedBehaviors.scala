package sample

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.util.Timeout
import sample.Warrior._

import scala.concurrent.duration.DurationInt

object Warrior {
  sealed trait Message

  case object Attack extends Message

  case object Defend extends Message

  case object NonSense extends Message

  case object ChangeToUnhandled extends Message

  case object ChangeToEmpty extends Message

  case object ChangeToIgnore extends Message

  def apply(name: String) = new Warrior(name)
}

class Warrior(name: String) {
  def doPrintln(s: String) = println(s"[$name]: $s")

  def defensive: Behavior[Message] = {
    Behaviors.setup { ctx =>
      Behaviors.receiveMessage {
        case Attack =>
          doPrintln("Changing from defensive to offensive")
          offensive
        case Defend =>
          doPrintln("Already in defensive mode.")
          Behaviors.same
        case ChangeToUnhandled =>
          doPrintln("Changing from defensive to unhandled.")
          Behaviors.unhandled
        case ChangeToEmpty =>
          doPrintln("Changing from defensive to empty.")
          Behaviors.empty
        case ChangeToIgnore =>
          doPrintln(s"Changing from defensive to ignore.")
          Behaviors.ignore
        case m =>
          doPrintln(s"I am in defensive mode and I don't understand the message: $m")
          Behaviors.ignore
      }
    }
  }

  def offensive: Behavior[Message] = {
    Behaviors.setup { ctx =>
      Behaviors.receiveMessage {
        case Defend =>
          doPrintln("Changing from offensive to defensive")
          defensive
        case Attack =>
          doPrintln("Already in offensive mode.")
          Behaviors.same
        case m =>
          doPrintln(s"I am in offensive mode and I don't understand the message: $m")
          Behaviors.ignore
      }
    }
  }
}

case object BattleGround {
  case object Message

  def running: Behavior[Message] = Behaviors.setup { ctx =>
    val warrior1 = ctx.spawn(Warrior("warrior1").offensive, "warrior1")
    implicit val resolveActorTimeout: Timeout = 3.seconds
    warrior1 ! Attack
    warrior1 ! Defend
    warrior1 ! NonSense
    warrior1 ! Attack
    Thread.sleep(2000)
    ctx.stop(warrior1)

    val warrior2 = ctx.spawn(Warrior("warrior2").offensive, "warrior2")
    warrior2 ! Attack
    warrior2 ! Defend
    warrior2 ! ChangeToUnhandled
    warrior2 ! Attack
    Thread.sleep(2000)
    ctx.stop(warrior2)

    val warrior3 = ctx.spawn(Warrior("warrior3").offensive, "warrior3")
    warrior3 ! Attack
    warrior3 ! Defend
    warrior3 ! ChangeToEmpty
    warrior3 ! Attack
    Thread.sleep(2000)
    ctx.stop(warrior3)

    val warrior4 = ctx.spawn(Warrior("warrior4").offensive, "warrior4")
    warrior4 ! Attack
    warrior4 ! Defend
    warrior4 ! ChangeToIgnore
    warrior4 ! Attack
    Thread.sleep(2000)
    ctx.stop(warrior4)

    Behaviors.stopped
  }
}

object PredefinedBehaviors extends App {
  println("Welcome to the warriors system!")
  ActorSystem(BattleGround.running, "battle-ground")
  println("nonblocking")
}
