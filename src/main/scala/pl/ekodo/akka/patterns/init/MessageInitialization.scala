package pl.ekodo.akka.patterns.init

import akka.actor._
import akka.actor.SupervisorStrategy._

/**
 * Created by marcin on 7/16/14.
 */
object MessageInitialization {
  case class ActorService(actorRef: ActorRef)
  object GetChild
  case class Child(actorRef: ActorRef)
}

import MessageInitialization._
class MessageInitialization extends Actor with ActorLogging {

  var service: ActorRef = _
  var child: ActorRef = _


  override def supervisorStrategy: SupervisorStrategy =  OneForOneStrategy() {
    case _: RuntimeException => Restart
    case _ => Restart
  }

  def uninitialized: Receive = {
    case ActorService(actorRef) =>
      service = actorRef
      child = context.actorOf(Props(classOf[ThrowingException],service))
      context.become(initialized)
  }

  def initialized: Receive = {
    case GetChild => sender() ! Child(child)
  }

  override def receive: Receive = uninitialized
}

object ThrowingException {
  object ThrowException
  object SayHello
}


import ThrowingException._
class ThrowingException(service: ActorRef) extends Actor with ActorLogging {

  log.info("actor constructor")
  log.info("service: " + service.path.name)

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    log.info("pre restart")
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.info("post restart")
  }

  override def receive: Receive = {
    case SayHello => service ! "hello"
    case ThrowException  => throw new RuntimeException
  }
}

class ActorService extends Actor with ActorLogging {
  override def receive: Actor.Receive = {
    case s: String => log.info(s"string print service : $s")
  }
}
