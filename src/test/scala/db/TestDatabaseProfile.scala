package db

import db.config.DatabaseProfile
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

/**
 * Created by Dikansky on 17.03.2016.
 */
trait TestDatabaseProfile extends DatabaseProfile{

  val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("database.test")

  val db = dbConfig.db

  val profile = dbConfig.driver

  println(s"Create db $db, ${Thread.currentThread().getName}")
}
