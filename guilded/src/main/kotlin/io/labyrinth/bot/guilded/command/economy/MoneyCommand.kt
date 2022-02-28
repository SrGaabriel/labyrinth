package io.labyrinth.bot.guilded.command.economy

import com.deck.extras.Emojis
import dev.gaabriel.clubs.common.struct.Command
import io.labyrinth.bot.guilded.command.LabyrinthCommandContext
import io.labyrinth.bot.guilded.service.CommandService

public val CommandService.money: Command<LabyrinthCommandContext> get() = command("money", "atm") {
    runs {
        val user = getLabyrinthUser()
        fancy("You currently have **${user.coins}** coins in your account!", Emojis.MONEY_WITH_WINGS)
    }
}