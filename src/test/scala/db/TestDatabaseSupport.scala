package db

import java.time.Instant

import db.config.DatabaseProfile
import db.table._
import shapeless.HNil


trait TestDatabaseSupport
  extends DatabaseProfile with UserTable with UserRoleTable with RoleTable with PermissionTable with RolePermissionTable with BankTable with PersonTable {
  self: DatabaseProfile =>

  import TestDatabaseSupport._
  import profile.api._


  val schema = roles.schema ++ users.schema ++ userRoles.schema ++ permissions.schema ++ rolePermissions.schema ++ banks.schema ++ persons.schema

  val rolesData = 1L.to(10L).map(i => RoleRow(s"role$i" , Some(i)))

  implicit val currentUser = TestUser

  val userData = Seq (
    UserRow(currentUser.username, currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, currentUser.bank.id, currentUser.phoneNumber, currentUser.additionalInfo, currentUser.id),
    UserRow("username13", currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, 13L, currentUser.phoneNumber, currentUser.additionalInfo, Some(13L)),
    UserRow("username3", currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, 3L, currentUser.phoneNumber, currentUser.additionalInfo , Some(3L)),
    UserRow("username5", currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, 5L, currentUser.phoneNumber, currentUser.additionalInfo , Some(5L))
  )

  val personData = Seq (
    PersonRow(currentUser.username, currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, currentUser.bank.id, currentUser.phoneNumber, currentUser.additionalInfo, currentUser.id),
    PersonRow("username13", currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, 13L, currentUser.phoneNumber, currentUser.additionalInfo, Some(13L)),
    PersonRow("username3", currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, 3L, currentUser.phoneNumber, currentUser.additionalInfo , Some(3L)),
    PersonRow("username5", currentUser.enabled, currentUser.name, currentUser.password, currentUser.email, 5L, currentUser.phoneNumber, currentUser.additionalInfo , Some(5L))
  )

  val u2rData = Seq(
    UserRoleRow(1L, 1L),
    UserRoleRow(1L, 2L),
    UserRoleRow(5L, 1L),
    UserRoleRow(5L, 2L)
  )

  val permissionData = Seq(
    PermissionRow("permission1" , 1L ),
    PermissionRow("permission2" ,(2L) ),
    PermissionRow("permission3", (3L) ),
    PermissionRow("permission4", (4L) ),
    PermissionRow("permission5" , (5L) ),
    PermissionRow("permission6", (6L) ),
    PermissionRow("permission7" , (7L) ),
    PermissionRow("permission8" , (8L) ),
    PermissionRow("permission9" , (9L) ),
    PermissionRow("MANAGE_USERS" , (10L) )
  )

  val r2pData = Seq(
    RolePermissionRow(1L ,1L ),
    RolePermissionRow(1L , 4L ),
    RolePermissionRow(1L , 3L ),
    RolePermissionRow(2L , 4L ),
    RolePermissionRow(2L , 10L ),
    RolePermissionRow(2L , 5L ),
    RolePermissionRow(3L , 1L )
  )

  val banksData = Seq(
    BankRow(13L , "bank13" , None),
    BankRow(currentUser.bank.id , currentUser.bank.name , currentUser.bank.parentId),
    BankRow(1L , "bank13-1" , Some(13L)),
    BankRow(2L , "bank13-2" , Some(13L)),
    BankRow(3L , "bank13-3" , Some(13L)),
    BankRow(4L , "bank13-3-4" , Some(3L)),
    BankRow(5L , "bank13-3-4-5" , Some(4L))
  )

  def createSchema() = {
    DBIO.seq(
      schema.create,
      banks ++= banksData,
      roles ++= rolesData,
      permissions ++= permissionData,
      rolePermissions ++= r2pData,
      users ++= userData,
      userRoles ++= u2rData,
      persons ++= personData
    )
  }

  def dropSchema() = {
    schema.drop
  }
}

object TestDatabaseSupport {
  val SharedTimeValue = Instant.now()
  val TestUserId = 1L
  val TestUser = User(
    "username",
    true,
    "name",
    "$2a$10$mzPmJ0IeFQlGlnBezsc9Xekj.Ux8yrzU4npPMDSQeCVtyGG8CkHxm",
    "email",
    Bank(14, "bank14", None),
    None,
    None,
    Set.empty,
    Some(TestUserId)
  )
}

