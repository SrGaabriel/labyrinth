package io.labyrinth.bot.guilded

import com.deck.core.DeckClient
import io.labyrinth.bot.guilded.database.DatabaseService
import io.labyrinth.bot.guilded.service.CommandService
import io.labyrinth.bot.guilded.service.GiveawayService
import io.labyrinth.bot.guilded.service.ServerService
import io.labyrinth.bot.guilded.service.UserService
import io.labyrinth.bot.guilded.task.GiveawayTask
import io.labyrinth.bot.guilded.util.LabyrinthConfig

public class LabyrinthBot(public val config: LabyrinthConfig) {
    public val client: DeckClient = DeckClient(config.token)

    public val userService: UserService = UserService()
    public val serverService: ServerService = ServerService()

    public val commandService: CommandService = CommandService(this)
    public val giveawayService: GiveawayService = GiveawayService(this)

    public suspend fun start() {
        val database = DatabaseService.config(config)
        database.connect()
        database.createTables()
        commandService.start()
        client.login()
        initTasks()
    }

    public suspend fun initTasks() {
        GiveawayTask(this).init()
    }
}