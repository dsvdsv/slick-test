package db.table

import db.config.DatabaseProfile

trait RoleTable extends TableComponent {
  self: DatabaseProfile =>

  import profile.api._

  case class RoleRow(name:String, id: Option[Long] = None)

  class Roles(tag: Tag) extends EntityTable[RoleRow](tag, "ROLES") {
    val id = column[Long]("ID", O.PrimaryKey)
    val name = column[String]("NAME")

    def * = (name, id.?) <> (RoleRow.tupled, RoleRow.unapply)

    def idx = index("IDX_ROLENAME", name, true)
  }

  object roles extends EntityQuery[RoleRow, Roles](new Roles(_)) {
    val sequence = Sequence[Long]("ROLE_SEQ")
  }

}
