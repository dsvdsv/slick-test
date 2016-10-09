package db

import java.time.Instant

import shapeless._
import syntax.std.tuple._
import db.api._
import db.config.DatabaseSupport
import db.table._
import shapeless.Nat._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future


class UserRepository(dbConfig: DatabaseConfig[JdbcProfile])
  extends DatabaseSupport(dbConfig)
    with ReadableRepository with TableRepository with UpdatableRepository with InsertableRepository
    with UserTable with RoleTable with PermissionTable with UserRoleTable with RolePermissionTable with BankTable {

  import profile.api._
  import UserRepository._

  type Id = Long
  type Entity = User
  type Row = (UserRow, Option[RoleRow], Option[PermissionRow], BankRow)

  private val baseQuery = for {
    ((((user, _), role),_), permission) <- users
      .joinLeft(userRoles).on(_.id === _.userId)
      .joinLeft(roles).on(_._2.map(_.roleId) === _.id)
      .joinLeft(rolePermissions).on(_._1._2.map(_.roleId) === _.roleId)
      .joinLeft(permissions).on(_._2.map(_.permissionId) === _.id)
    bank <- banks if bank.id === user.bankId
  } yield (user, role, permission, bank)

  protected val byIdQuery = Compiled { userId: Rep[Long] =>
    for {
      (user, role, permission, bank) <- baseQuery if user.id === userId
    } yield (user, role, permission, bank)
  }
  protected val byUsernameQuery = Compiled{ username: Rep[String] =>
    for {
      (user, role, permission, bank) <- baseQuery if user.username === username
    } yield (user, role, permission, bank)
  }

  protected val allQuery = Compiled{baseQuery}

  def update(username: String, password: String): Future[Boolean] = {
    val query = users.filter(u => u.username === username)
      .map(_.password).update(password)

    db.run(query)
      .map(_ == 1)
  }

  protected def listAction = {
    baseQuery.result
  }

  protected def byTextAction(text: String) = {
    baseQuery
      .filter {
        case (user, _, _, _) =>
          (user.name.toUpperCase.like(text) ||
            user.username.toUpperCase.like(text) ||
            user.id.asColumnOf[String] === text)
      }
      .result
  }

  protected def byTextWithBankIdAction(text: String, bankId: Long) = {
    baseQuery
      .filter {
        case (user, _, _, _) =>
          (user.name.toUpperCase.like(text) ||
            user.username.toUpperCase.like(text) ||
            user.id.asColumnOf[String] === text)
      }
      .result
  }

  protected def byBankIdAction(bankId: Long) = {
    baseQuery
      .filter {
        case (user, _, _, _) => user.bankId === bankId
      }
      .result
  }

  protected def byIdAction(id: Id) = {
    baseQuery
      .filter {
        case (user, _, _, _) => user.id === id
      }
      .result
  }

  protected def insertAction(user: User) = {
    (for {
      id <- Query(users.sequence.next).result.head
      newValue = UserRow(user.username, user.enabled, user.name, user.password, user.email, user.bank.id, user.phoneNumber, user.additionalInfo, Some(id))
      _ <- users += newValue
      _ <- userRoles ++= user.roles.map(r => UserRoleRow(id, r.id.get))
      user <- byIdQuery(id).result
    } yield user).transactionally
  }

  protected def updateAction(user: User) = {
//    val nv = UserRow(user.username, user.enabled, user.name, user.email, user.bank.id, user.phoneNumber, user.additionalInfo, user.id)
//    val userRowGen = LabelledGeneric[UserRow]
//    users
//      .filter(_.id === user.id)
//      .result
//      .flatMap { userRow =>
//        val e = userRowGen.to(userRow)
//        val n = userRowGen.to(nv)
//      }
    for {
      res <- users.filter(u => u.id === user.id)
        .map(r => (r.username, r.enabled, r.name, r.email, r.bankId, r.phoneNumber, r.additionalInfo))
        .update((user.username, user.enabled, user.name, user.email, user.bank.id, user.phoneNumber, user.additionalInfo))

      if res == 1
      _ <- userRoles.filter(_.userId === user.id).delete
      _ <- userRoles ++= user.roles.map(r => UserRoleRow(user.id.get, r.id.get))
    } yield res
  }

  protected def buildEntity(row: Seq[Row]): Iterable[Entity] = {
    val role2permission = group[(Option[RoleRow], Option[PermissionRow]), Option[RoleRow], Option[PermissionRow]](
      row.map(r => (r._2, r._3)).filter(_._1.isDefined),
      elem => elem._1,
      elem => elem._2,
      key => key.map(_.at(1)).flatten.get
    )
    val roleMap = for {
      (r, r2p) <- role2permission
    } yield {
      val permissions = r2p
        .flatten
        .map(p => Permission(p.name, p.id))
        .toSet
      val role = Role(id = r.get.id, name = r.get.name, permissions = permissions)
      (role.id, role)
    }
    val persons = group[Row, (UserRow, BankRow), Option[RoleRow]](
      row,
      elem => (elem._1, elem._4),
      elem => elem._2,
      key => key._1.id.get
    )
    for {
      ((u, bank), u2r) <- persons
    } yield {
      val userRoles = u2r.flatten.toSet
      User(
        u.username,
        u.enabled,
        u.name,
        u.password,
        u.email,
        Bank(bank.id, bank.name, bank.parentId),
        u.phoneNumber,
        u.additionalInfo,
        roles = userRoles.map((r: RoleRow) => roleMap(r.id)),
        id = u.id
      )
    }
  }

  def byUsername(username: String): Future[Option[Entity]] = {
    db.run(byUsernameQuery(username).result)
      .map(buildEntity)
      .map(_.headOption)
  }

  def byBankId(bankId: Long): Future[Iterable[Entity]] = {
    db.run(byBankIdAction(bankId))
      .map(buildEntity)
  }

  def all: Future[Iterable[Entity]] = {
    db.run(allQuery.result)
      .map(buildEntity)
  }

  def byTextWithBankId(text: String, bankId: Long): Future[Iterable[Entity]] = {
    val pattern = s"%${text.toUpperCase}%"
    db.run(byTextWithBankIdAction(pattern, bankId))
      .map(buildEntity)
  }
}

private object UserRepository {
  val bankGen = Generic[Bank]
  val permissionGen= Generic[Permission]
  val userGet = Generic[User]

  val bankId = Nat(5)
  val createTime = Nat(8)
}

