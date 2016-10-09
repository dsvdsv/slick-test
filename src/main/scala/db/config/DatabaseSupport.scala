package db.config

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

abstract class DatabaseSupport(val config: DatabaseConfig[JdbcProfile]) extends DatabaseProfile {

  val profile = config.driver

  val db = config.db

  implicit val executor = db.executor.executionContext
}

