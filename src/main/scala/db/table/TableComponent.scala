package db.table

import java.sql.Timestamp
import java.time.Instant

import db.config.DatabaseProfile

trait TableComponent {
  self: DatabaseProfile =>

  import profile.api._

  /**
    * Instant to Timestamp mapper.
    */
  implicit val instantMapper = MappedColumnType.base[Instant, Timestamp](
    inst => if (inst == null) null else Timestamp.from(inst),
    ts => if (ts == null) null else ts.toInstant
  )

  abstract class EntityTable[E](tag: Tag, tableName: String) extends Table[E](tag, tableName) {
    def id: Rep[Long]
  }

  abstract class EntityQuery[E, T <: EntityTable[E]](cons: Tag => T)
    extends TableQuery(cons) {

    val byId = this.findBy(_.id)
  }
}
