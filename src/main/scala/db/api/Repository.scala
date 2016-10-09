package db.api

import db.config.DatabaseProfile

/**
 * Created by Dikansky on 09.02.2016.
 */
trait Repository {
  self: DatabaseProfile =>

  type Entity
  type Row

  protected def buildEntity(row: Seq[Row]): Iterable[Entity]
}
