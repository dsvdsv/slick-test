package db.table

import db.config.DatabaseProfile
import shapeless._
import syntax._
import syntax.std.tuple._

import scala.collection.mutable
import scala.concurrent.ExecutionContext

trait PersonTable extends TableComponent with BankTable {
  self: DatabaseProfile =>

  import profile.api._

  case class PersonRow(
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

  class Persons(tag: Tag) extends EntityTable[PersonRow](tag, "PERSONS") {
    val id = column[Long]("ID", O.PrimaryKey)
    val username = column[String]("USERNAME")
    val enabled = column[Boolean]("ENABLED")
    val name = column[String]("NAME")
    val password = column[String]("PASSWORD")
    val email = column[String]("EMAIL")
    val bankId = column[Long]("BANK_ID")
    val phoneNumber = column[Option[String]]("PHONE_NUMBER")
    val additionalInfo = column[Option[String]]("ADDITIONAL_INFO")

    def * = (username, enabled, name, password, email, bankId, phoneNumber, additionalInfo, id.?) <> (PersonRow.tupled, PersonRow.unapply)

    def idx = index("IDX_PERSONNAME", username, true)

    val bank = foreignKey("person_bank_fk", bankId, banks)(_.id)

  }

  object persons extends EntityQuery[PersonRow, Persons](new Persons(_)) {
//    val sequence = Sequence[Long]("USER_SEQ")
    val byUsername = this.findBy(_.username)

    val personRowGen = Generic[PersonRow]

    val columns = (baseTableRow.username, baseTableRow.enabled, baseTableRow.name, baseTableRow.password, baseTableRow.email, baseTableRow.bankId, baseTableRow.phoneNumber, baseTableRow.additionalInfo, baseTableRow.id)
/*
    def update(value: PersonRow)(implicit executor: ExecutionContext): DBIOAction[Int, NoStream, Effect] = {
      val findQuery = this.filter { _.id === value.id }

      findQuery.result
        .flatMap { col =>
          val exist = col.headOption.get
          val nhl = personRowGen.to(value)
          val chl = columns.productElements
          val indexes = mutable.ArrayBuffer[Int]()

          var i = 0
          while (i<exist.productArity) {
            if (exist.productElement(i)!= value.productElement(i)) {
              indexes += i
            }
            i = i + 1
          }

          chl.selectMany(indexes.toArray:_*)
//          val ehl = personRowGen.to(exist)
//          val nhl = personRowGen.to(value)
//          val chl = columns.productElements
//          var indexses = mutable.ArrayBuffer[Int]()
//          var i = 0
//          var ehead = ehl.head
//          var nhead = nhl.head
//          do {
//
//            if (ehead!= nhead) {
//              indexses += i
//            }
//
//            i = i + 1
//            nhead = nhl.tail
//            ehead = ehl.tail
//          } while (nhead != HNil)
          DBIO.successful(1)
        }
    }
    */
  }

}
