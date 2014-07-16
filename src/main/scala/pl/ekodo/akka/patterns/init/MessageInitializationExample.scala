package pl.ekodo.akka.patterns.init

import akka.actor.{Props, ActorSystem}
import pl.ekodo.akka.patterns.init.MessageInitialization.{ActorService, Child, GetChild}
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import pl.ekodo.akka.patterns.init.ThrowingException.{ThrowException, SayHello}
import akka.util.Timeout

/**
 * Created by marcin on 7/16/14.
 */
object MessageInitializationExample extends App {

  val sys = ActorSystem("actorSystem")
  val service = sys.actorOf(Props[ActorService])
  val parent = sys.actorOf(Props[MessageInitialization])
  parent ! ActorService(service)
  implicit val timeout = Timeout(1 second)
  val child = Await.result(parent ? GetChild, 1 second).asInstanceOf[Child]
  child.actorRef ! SayHello
  child.actorRef ! ThrowException

  child.actorRef ! SayHello

  Thread.sleep(200)
  sys.shutdown()
}
