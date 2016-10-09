package db.config

import slick.driver.JdbcProfile

trait DatabaseProfile {
  val profile: JdbcProfile
}