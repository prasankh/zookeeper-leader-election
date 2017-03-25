Example of creating a distributed application where one instance of node gets elected as the master using message passing between Akka's actor.


Running the Code:
"sbt run" will run an instance of node.
or
create fat jar using "sbt assembly" and run with java -jar Leader_Election-assembly-1.0.jar

you will find the fat jar in /target/scala-2.10 folder.

You need to start Zookeeper before running the application.
