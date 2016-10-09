package db

import java.sql.SQLException

import scala.concurrent.ExecutionContext.Implicits.global

class UserRepositorySpec extends RepositorySpec {
  import profile.api._

  import TestDatabaseSupport._

  val repository = new UserRepository(dbConfig)

  test("byId should return entity with given id") {
    val user = repository.byId(TestUserId).futureValue
    assert(user.exists(_.id.contains(TestUserId)))
    assert(user.exists(_.bank == Bank(14, "bank14", None)))
    assert(user.exists(_.roles.size == 2))
  }

  test("byText with valid text should return collection of entity") {
    val users = repository.byText("usern").futureValue
    assert(users.exists(_.id.contains(TestUserId)))
  }

  test("list should collection of entity") {
    val users = repository.list.futureValue
    assert(users.size == 4L )
    assert(users.exists(_.id.contains(TestUserId)))
  }

  test("update password should return true") {
    assert(repository.update("username", "pwd").futureValue) // user exist
    assert(!repository.update("username1", "pwd").futureValue) // user not exist
  }

  test("create should return new user") {
    val user = User(
      "newUsername",
      true,
      "newUser",
      "pwd",
      "email",
      Bank(5, "bank13-3-4-5", Some(4)),
      Some("22323111"),
      Some("text"),
      Set(Role(id = Some(1), name = "role1"))
    )
    val newUser = repository.insert(user).futureValue

    val isUserRoleInserted = db.run(userRoles
      .filter(r => r.roleId === 1L && r.userId === newUser.id.get.bind)
      .exists
      .result
    ).futureValue

    assert(isUserRoleInserted)
    assert(repository.byId(newUser.id.get).futureValue.contains(newUser))
  }

  test("create with exist username should return exception") {
    val user = User(
      "username",
      true,
      "newUser",
      "pwd",
      "email",
      Bank(5L, "bank13-3-4-5", Some(4)),
      Some("22323111"),
      Some("text"),
      Set(Role(id = Some(1), name = "role1"))
    )
    val w = new Waiter
    repository.insert(user)
      .onFailure{
        case e:SQLException => w.dismiss()
      }
    w.await()
  }

  test("create with fake role id should return exception") {
    val user = User(
      "newUsername",
      true,
      "newUser",
      "pwd",
      "email",
      Bank(5L, "bank13-3-4-5", Some(4)),
      Some("22323111"),
      Some("text"),
      Set(new Role(9999))
    )
    val w = new Waiter
    repository.insert(user)
      .onFailure{
        case e:SQLException => w.dismiss()
      }

    w.await()
  }

  test("update with fake role should return exception") {
    val user = repository.byId(TestUserId).futureValue.get

    val modifiedUser = user.copy(email = "newEmail", roles = (user.roles + (new Role(39999))))

    val w = new Waiter
    repository.update(modifiedUser)
      .onFailure{
        case e:SQLException => w.dismiss()
      }
    w.await()
  }

  test("update should update user") {
    val user = repository.byId(TestUserId).futureValue.get

    val modifiedUser = user.copy(email = "newEmail", roles = (user.roles + Role(id = Some(3L), name = "role3")))

    val isUpdated = repository.update(modifiedUser).futureValue

    assert(isUpdated)
    assert(db.run(users.filter(r => r.id === TestUserId && r.email === "newEmail").exists.result).futureValue)
    assert(db.run(userRoles.filter(r=> r.userId === TestUserId && r.roleId === 3L).exists.result).futureValue)
  }
}
