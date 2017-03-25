package election

import akka.actor.ActorSystem
import akka.actor.Props

object start {
  def main(args: Array[String]): Unit = {

    val actorSystem = ActorSystem("HadesSystem")
    val node = actorSystem.actorOf(Props(new HadesNode()), name = "Nodes")
    node ! "UP"
  }
}
