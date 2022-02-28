package io.labyrinth.bot.guilded.database

import com.zaxxer.hikari.HikariDataSource
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerGiveawaysTable
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerTable
import io.labyrinth.bot.guilded.database.entity.LabyrinthUserTable
import io.labyrinth.bot.guilded.util.LabyrinthConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

public class DatabaseService(
    public val host: String,
    public val port: String,
    public val username: String,
    public val password: String,
    public val database: String
) {
    public fun connect() {
        val dataSource = HikariDataSource().apply {
            dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
            jdbcUrl = "jdbc:postgresql://$host:$port/$database"
            username = this@DatabaseService.username
            password = this@DatabaseService.password
        }
        Database.connect(dataSource)
    }

    public suspend fun createTables(): Unit = newSuspendedTransaction {
        SchemaUtils.drop(
            LabyrinthUserTable,
            LabyrinthServerTable,
            LabyrinthServerGiveawaysTable
        )
        SchemaUtils.createMissingTablesAndColumns(
            LabyrinthUserTable,
            LabyrinthServerTable,
            LabyrinthServerGiveawaysTable
        )
    }

    public companion object {
        public fun config(config: LabyrinthConfig): DatabaseService = DatabaseService(
            host = config.database.host,
            port = config.database.port,
            username = config.database.username,
            password = config.database.password,
            database = config.database.database,
        )
    }
}