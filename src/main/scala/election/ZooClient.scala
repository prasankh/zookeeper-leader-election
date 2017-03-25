package election

import akka.actor.Actor

import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.proto.WatcherEvent
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.ZooDefs.Ids
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import akka.actor.ActorRef

class ZooClient extends Actor {
  val zk = new ZooKeeper("localhost", 3000, watcherCur)
  val NodeDeleted = Watcher.Event.EventType.NodeDeleted
  var hadesNode:ActorRef=null
  
  val watcherCur = new Watcher {
    def process(event: WatchedEvent) {
      val eventType = event.getType
      if (eventType.equals(NodeDeleted)) {
        hadesNode ! localMasterDeleted
      }
    }
  }

  def receive = {
    case keepEyeOn: SetWatcher =>
      println("Watching Over Node:"+keepEyeOn.node)
      zk.exists(keepEyeOn.node, true)
          
    case rootNode:RootNodeCreate =>
      hadesNode=sender()
      try {
        val added = zk.create(rootNode.name, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT)
        if (added != null) {
          println(rootNode.name + " Node added successfully")
        }
      } catch {
        case k: KeeperException => println(rootNode.name + " Node already exist")
      }
    case newnode: Newnode =>
      var added: String = null
      try {
        added = zk.create(newnode.name, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL)
        if (added != null) {
          println(added + " Node added successfully")
        }
      } catch {
        case k: KeeperException => println(newnode + " Node already exist")
      }
      sender() ! AddedNode(added)
    case gc: GetChildren =>
      println("getting children")
      var childNodes: java.util.List[String] = null
      try {
        childNodes = zk.getChildren(gc.node, false)
      } catch {
        case i: IllegalStateException => println(i)
      }
      sender() ! Chnodes(childNodes)
    
  }
}

case class Chnodes(list: java.util.List[String])
case class AddedNode(name: String)
case object localMasterDeleted