package db.api

import db.config.DatabaseSupport
import slick.dbio.Effect.Write
import slick.dbio.{DBIOAction, NoStream}

import scala.concurrent.Future

/**
 * Created by Dikansky on 26.02.2016.
 */
trait UpdatableRepository extends TableRepository {
  self: DatabaseSupport =>

  def update(entity: Entity): Future[Boolean] = {
    db.run(updateAction(entity))
      .map(_ == 1)
  }

  protected def updateAction(entity: Entity): DBIOAction[Int, NoStream, Nothing with Write]
}
