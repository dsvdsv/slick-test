package db

import shapeless._
import syntax.std.tuple._

class UpdateSpec extends RepositorySpec {

  import profile.api._

  test("update1 should update user") {
    println(persons.baseTableRow.getClass)

    val i = 1
    def f(u:Persons) = {
      () :+ u.username :+ u.email
    }

    def v = {
      () :+ "testupdate" :+ "test"
    }

    val action = persons
      .filter(_.id === 5L)
      .map(f)
      .update(v)

    val result = db.run(action)

    assert(result.futureValue > 0)
    assert(db.run(persons.filter(r => r.id === 5L && r.username === "testupdate").exists.result).futureValue)
  }

//  test("test hlist to tuple") {
//    val hlist = 1 :: 2 :: 3 :: 4 :: 5 :: 6 :: 7 :: 8 :: 9 :: 10 :: 11 :: 12 :: 13 :: 14 :: 15 :: 16 :: 17 :: 18 :: 19 :: 20 :: 21 :: 22 :: 23 :: HNil
//
//    val tuple = hlist.tupled
//
//    assert(tuple != null)
//  }
}
