package io.labyrinth.bot.guilded.command.economy

import com.deck.extras.Emojis
import dev.gaabriel.clubs.common.struct.Command
import io.labyrinth.bot.guilded.command.LabyrinthCommandContext
import io.labyrinth.bot.guilded.service.CommandService
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.random.Random
import kotlin.random.nextInt

public val CommandService.daily: Command<LabyrinthCommandContext> get() = command("daily") {
    runs {
        val user = getLabyrinthUser()
        if (!user.isDailyAvailable()) {
            fancy("You have already collected your daily cash today. Come back tomorrow for more!", Emojis.X)
            return@runs
        }
        val money = Random.nextInt(1000..7500)
        newSuspendedTransaction {
            user.daily = System.currentTimeMillis()
            user.coins += money
        }
        fancy("Congratulations! You earned **$money** coins in our daily roulette!", Emojis.GIFT)
    }
}