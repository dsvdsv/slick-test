package db.api

import db.config.DatabaseSupport
import slick.dbio.Effect.Write

import scala.concurrent.Future

/**
 * Created by Dikansky on 26.02.2016.
 */
trait InsertableRepository extends TableRepository {
  self: DatabaseSupport =>

  import profile.api._

  def insert(entity: Entity): Future[Entity] = {
    db.run(insertAction(entity))
      .map(buildEntity)
      .map(_.head)
  }

  protected def insertAction(entity: Entity): DBIOAction[Seq[Row], NoStream, Nothing with Write]
}

