package db.table

import db.config.DatabaseProfile


trait RolePermissionTable extends TableComponent with RoleTable with PermissionTable {
  self: DatabaseProfile with PermissionTable with RoleTable =>

  import profile.api._

  case class RolePermissionRow(roleId: Long, permissionId: Long)

  class RolePermissions(tag: Tag) extends Table[RolePermissionRow](tag, "ROLES_PERMISSIONS") {
    val roleId = column[Long]("ROLES_ID")
    val permissionId = column[Long]("PERMISSIONS_ID")

    val pk = primaryKey("role_permission_pk", (roleId, permissionId))
    val permission = foreignKey("role_permission_permission_fk", permissionId, permissions)(_.id)
    val role = foreignKey("role_permission_role_fk", roleId, roles)(_.id)

    def * = (roleId, permissionId) <> (RolePermissionRow.tupled, RolePermissionRow.unapply)
  }

  object rolePermissions extends TableQuery[RolePermissions](new RolePermissions(_)) {
    val byRoleId = this.findBy(_.roleId)
    val byPermissionId = this.findBy(_.permissionId)
  }

}
