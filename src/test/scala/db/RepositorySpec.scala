package db

import org.scalatest._
import org.scalatest.concurrent.{AsyncAssertions, ScalaFutures}
import org.scalatest.time.{Seconds, Span}

abstract class RepositorySpec extends FunSuite with Matchers with BeforeAndAfterAll with AsyncAssertions
  with ScalaFutures with TestDatabaseProfile with TestDatabaseSupport{

  implicit override val patienceConfig = PatienceConfig(timeout = Span(10, Seconds))

  import profile.api._


//  before {
//    db.run(createSchema()).futureValue
//  }
//
//  after {
//    db.run(dropSchema()).futureValue
//  }

  override protected def beforeAll(): Unit = {
    println("Create schema " + Thread.currentThread().getName)
    db.run(createSchema()).futureValue
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    println("Drop schema "  + Thread.currentThread().getName)
    db.run(dropSchema()).futureValue
    super.afterAll()
  }
}
