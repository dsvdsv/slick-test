package db.table

import db.config.DatabaseProfile

trait BankTable extends TableComponent {
  self: DatabaseProfile =>

  import profile.api._

  case class BankRow(id: Long, name: String, parentId:Option[Long])

  class Banks(tag: Tag) extends EntityTable[BankRow](tag, "BANKS") {
    val id = column[Long]("ID", O.PrimaryKey)
    val name = column[String]("NAME")
    val parentId = column[Option[Long]]("PARENT_ID")

    def * = (id, name, parentId) <> (BankRow.tupled, BankRow.unapply)

    def parent = foreignKey("BANK_PARENT_FK", parentId, banks)(_.id.?)

    def idx = index("IDX_BANKNAME", name, true)
  }

  object banks extends EntityQuery[BankRow, Banks](new Banks(_)) {

  }

}