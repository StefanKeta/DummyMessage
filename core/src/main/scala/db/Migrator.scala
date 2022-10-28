package db

import cats.effect.Sync
import config.DbConfig
import org.flywaydb.core.Flyway

object Migrator {
  def migrate[F[_]: Sync](dbConfig: DbConfig) =
    Sync[F].delay {
      val url =
        s"jdbc:postgresql://${dbConfig.host}:${dbConfig.port}/${dbConfig.name}"
      Flyway
        .configure()
        .dataSource(url, dbConfig.username, dbConfig.password)
        .locations("classpath:sql")
        .table("db-changelog")
        .baselineOnMigrate(true)
        .load()
        .migrate()
        .migrations
        .size()
    }

}
