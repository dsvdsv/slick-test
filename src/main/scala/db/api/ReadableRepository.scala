package db.api

import db.config.DatabaseSupport
import slick.dbio.Effect.Read

import scala.concurrent.Future


trait ReadableRepository extends Repository {
  self: DatabaseSupport =>

  import profile.api._

  def list: Future[Iterable[Entity]] = {
    db.run(listAction)
      .map(buildEntity)
  }

  def byText(text: String): Future[Iterable[Entity]] = {
    val pattern = s"%${text.toUpperCase}%"
    db.run(byTextAction(pattern))
      .map(buildEntity)
  }

  protected def listAction: DBIOAction[Seq[Row], NoStream, Read]

  protected def byTextAction(text: String): DBIOAction[Seq[Row], NoStream, Read]

}
