package db.table

import db.config.DatabaseProfile


trait UserTable extends TableComponent with BankTable {
  self: DatabaseProfile =>

  import profile.api._

  case class UserRow(
         username: String,
         enabled: Boolean,
         name: String,
         password: String,
         email: String,
         bankId: Long,
         phoneNumber: Option[String] = None,
         additionalInfo: Option[String] = None,
         id: Option[Long] = None
  )

  class Users(tag: Tag) extends EntityTable[UserRow](tag, "USERS") {
    val id = column[Long]("ID", O.PrimaryKey)
    val username = column[String]("USERNAME")
    val enabled = column[Boolean]("ENABLED")
    val name = column[String]("NAME")
    val password = column[String]("PASSWORD")
    val email = column[String]("EMAIL")
    val bankId = column[Long]("BANK_ID")
    val phoneNumber = column[Option[String]]("PHONE_NUMBER")
    val additionalInfo = column[Option[String]]("ADDITIONAL_INFO")

    def * = (username, enabled, name, password, email, bankId, phoneNumber, additionalInfo, id.?) <> (UserRow.tupled, UserRow.unapply)

    def idx = index("IDX_USERNAME", username, true)

    val bank = foreignKey("user_bank_fk", bankId, banks)(_.id)

  }

  object users extends EntityQuery[UserRow, Users](new Users(_)) {
    val sequence = Sequence[Long]("USER_SEQ")
    val byUsername = this.findBy(_.username)
  }

}
