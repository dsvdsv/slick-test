package db.table

import db.config.DatabaseProfile

trait PermissionTable extends TableComponent {
  self: DatabaseProfile =>

  import profile.api._

  case class PermissionRow(name: String, id: Long)

  class Permissions(tag: Tag) extends EntityTable[PermissionRow](tag, "PERMISSIONS") {
    val id = column[Long]("ID", O.PrimaryKey)
    val name = column[String]("NAME")

    def * = (name, id) <> (PermissionRow.tupled, PermissionRow.unapply)
  }

  object permissions extends EntityQuery[PermissionRow, Permissions](new Permissions(_)) {
  }

}
