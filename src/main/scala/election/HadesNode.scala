package election

import akka.actor.Actor
import akka.actor.Props


import scala.util.Random
import scala.collection.JavaConversions._
import org.apache.zookeeper.Watcher
import scala.collection.JavaConversions._

class HadesNode extends Actor {

  val zoo = context.system.actorOf(Props(new ZooClient), name = "zookeeperClient")
  val ROOTNODE = "/election"
  val Node_prefix = "/node_"
  var currnode: String = null
  
  def attemptForLeaderPosition(listofChild: java.util.List[String], node: String): (String) = {
    var watchOverPath: String = null
    val sortedList = listofChild.sortWith(_ < _)
    println("Running instances:"+sortedList.toList)
    val index = sortedList.indexOf(node.split("/")(2))
    if (index == 0) {
      println("I, NodeID:" + node + " am the new leader")
    } else {
      watchOverPath = ROOTNODE + "/" + sortedList(index - 1)
    }
    return watchOverPath
  }

  def receive = {

    case "UP" =>
      val nodeID = Node_prefix
      zoo ! RootNodeCreate(ROOTNODE)
      zoo ! Newnode(ROOTNODE + nodeID)
      zoo ! GetChildren(ROOTNODE)

    case childs: Chnodes =>
      val watchOn = attemptForLeaderPosition(childs.list, currnode)
      if (watchOn != null)
        zoo ! SetWatcher(watchOn)
    case nodecur: AddedNode =>
      currnode = nodecur.name
      println("Current Node is :" + currnode)
    case localMasterDeleted =>
      zoo ! GetChildren(ROOTNODE)

  }
}

case class Newnode(name: String)
case class GetChildren(node: String)
case class SetWatcher(node: String)
case class RootNodeCreate(name:String)