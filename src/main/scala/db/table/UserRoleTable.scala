package db.table

import db.config.DatabaseProfile

trait UserRoleTable extends TableComponent with UserTable with RoleTable {
  self: DatabaseProfile =>

  import profile.api._

  case class UserRoleRow(userId: Long, roleId: Long)

  class UserRoles(tag: Tag) extends Table[UserRoleRow](tag, "USERS_ROLES") {
    val userId = column[Long]("USERS_ID")
    val roleId = column[Long]("ROLES_ID")

    val pk = primaryKey("user_roles_pk", (userId, roleId))
    val user = foreignKey("user_roles_user_fk", userId, users)(_.id)
    val role = foreignKey("user_roles_role_fk", roleId, roles)(_.id)

    def * = (userId, roleId) <> (UserRoleRow.tupled, UserRoleRow.unapply)
  }

  object userRoles extends TableQuery[UserRoles](new UserRoles(_)) {
    val byUserId = this.findBy(_.userId)
    val byRoleId = this.findBy(_.roleId)
  }

}
