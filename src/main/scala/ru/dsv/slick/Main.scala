package ru.dsv.slick

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by dsvdsv on 02.07.16.
  */
object Main extends App{


  // Create an in-memory H2 database;
  val dbConfig:DatabaseConfig[JdbcProfile]= DatabaseConfig.forConfig("database.test")

  val db = dbConfig.db


  import dbConfig.driver.api._

  // Case class representing a row in our table:
  final case class Message(
                            sender:  String,
                            content: String,
                            id:      Long = 0L)

  // Helper method for creating test data:
  def freshTestData = Seq(
    Message("Dave", "Hello, HAL. Do you read me, HAL?"),
    Message("HAL",  "Affirmative, Dave. I read you."),
    Message("Dave", "Open the pod bay doors, HAL."),
    Message("HAL",  "I'm sorry, Dave. I'm afraid I can't do that.")
  )

  // Schema for the "message" table:
  final class MessageTable(tag: Tag)
    extends Table[Message](tag, "MESSAGE") {

    def id      = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def sender  = column[String]("SENDER")
    def content = column[String]("CONTENT")

    def * = (sender, content, id) <> (Message.tupled, Message.unapply)
  }

  // Base query for querying the messages table:
  lazy val messages = TableQuery[MessageTable]

  // An example query that selects a subset of messages:
  val halSays = messages.filter(_.sender === "HAL")


  // Helper method for running a query in this example file:
  def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 20 seconds)

   //Create the "messages" table:
  println("Creating database table")
  exec(messages.schema.create)

  exec(messages.insertOrUpdate(Message("test", "test", 15)))

  val a = messages += Message("Stefan", randomString(1024))

  val res = exec(a.asTry)
  println(s"\n insert result $res")

  db.run(a)
     .onFailure{
       case t => {
         println(s"\n insert result $t")
       }
     }
//  val messageWithId = exec(
//    (messages returning messages.map(_.id)
//      into ((message,id) => message.copy(id=id))
//      ) += Message("Stefan", randomString(1024))
//  )
//  println(s"\nReturning id $messageWithId")

  // Create and insert the test data:
//  println("\nInserting test data")
//  exec(messages ++= freshTestData)

  // Run the test query and print the results:
  println("\nSelecting all messages:")
  exec( messages.result ) foreach { println }

  println("\nSelecting only messages from HAL:")
  exec( halSays.result ) foreach { println }

  def randomString(length: Int) = scala.util.Random.alphanumeric.take(length).mkString
}
