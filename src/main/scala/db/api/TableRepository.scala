package db.api

import db.config.DatabaseSupport
import slick.dbio.Effect.Read

import scala.collection.mutable
import scala.concurrent.Future

/**
 * Created by dsvdsv on 23.02.16.
 */
trait TableRepository extends Repository {
  self: DatabaseSupport =>

  import profile.api._

  type Id

  def byId(id: Id): Future[Option[Entity]] = {
    db.run(byIdAction(id))
      .map(buildEntity)
      .map(_.headOption)
  }

  def existsById(id: Id): Future[Boolean] = {
    db.run(byIdAction(id))
      .map(_.nonEmpty)
  }

  protected def byIdAction(id: Id): DBIOAction[Seq[Row], NoStream, Read]

  protected def group[T, Key, Value](
    seq: Iterable[T],
    kmap: T => Key,
    vmap: T => Value,
    idmap: Key => Id
  ): collection.Map[Key, Seq[Value]] = {
    val map = new mutable.LinkedHashMap[Key, mutable.ArrayBuffer[Value]]() {
      override protected def elemHashCode(key: Key) = idmap(key).hashCode()
    }

    for (elem <- seq) {
      val key = kmap(elem)
      val tickets = map.getOrElseUpdate(key, mutable.ArrayBuffer.empty[Value])
      tickets += vmap(elem)
    }
    map
  }
}
