package io.labyrinth.bot.guilded.service

import com.deck.core.DeckClient
import dev.gaabriel.clubs.bot.BotClubsInstance
import dev.gaabriel.clubs.common.ClubsInstance
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.newCommand
import io.labyrinth.bot.guilded.LabyrinthBot
import io.labyrinth.bot.guilded.command.LabyrinthCommandContext
import io.labyrinth.bot.guilded.command.LabyrinthCommandHandler
import io.labyrinth.bot.guilded.command.economy.daily
import io.labyrinth.bot.guilded.command.economy.money
import io.labyrinth.bot.guilded.command.misc.giveaway

public class CommandService(private val labyrinth: LabyrinthBot) {
    public val clubs: ClubsInstance<DeckClient> = BotClubsInstance {
        prefix = PREFIX
        handler = LabyrinthCommandHandler(labyrinth, failureHandler, argumentParser)
    }

    public suspend fun start() {
        clubs.register(daily)
        clubs.register(money)
        clubs.register(giveaway)
        clubs.start(labyrinth.client)
    }

    public fun command(
        vararg names: String,
        builder: Command<LabyrinthCommandContext>.() -> Unit
    ): Command<LabyrinthCommandContext> = newCommand(names = names, builder = builder)

    public companion object {
        public const val PREFIX: String = "+"
    }
}