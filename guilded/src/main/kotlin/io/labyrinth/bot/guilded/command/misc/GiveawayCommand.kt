package io.labyrinth.bot.guilded.command.misc

import com.deck.core.util.sendMessage
import com.deck.extras.Emojis
import com.deck.extras.conversation.type.conversate
import dev.gaabriel.clubs.common.struct.Command
import io.labyrinth.bot.guilded.command.LabyrinthCommandContext
import io.labyrinth.bot.guilded.database.entity.LabyrinthServer
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerGiveaway
import io.labyrinth.bot.guilded.service.CommandService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.time.Duration.Companion.seconds

public val CommandService.giveaway: Command<LabyrinthCommandContext> get() = command("giveaway") {
    command("start") {
        runs {
            var name: String
            var description: String
            var endDate: Instant
            user.conversate(channel) {
                name = (ask("${Emojis.MEMO} **|** What's the giveaway name?")
                    .awaitReply(60_000)
                    ?: return@conversate timeout())
                    .content
                if (name.length > 32)
                    return@conversate run {
                        channel.sendMessage("${Emojis.X} **|** The giveaway name can't have more than 32 characters.")
                        end()
                    }
                description = (ask("${Emojis.MEMO} **|** What's the giveaway description?")
                    .awaitReply(90_000)
                    ?: return@conversate timeout())
                    .content
                endDate = Clock.System.now() + (30).seconds
                val giveaway = startGiveaway(name, getLabyrinthServer()!!, endDate)
                channel.sendMessage(
                        """
                    __**$name | GIVEAWAY #${giveaway.localId}**__
                    
                    **INFO**: `$description`
                    
                    To participate you just need to execute `${CommandService.PREFIX}giveaway join ${giveaway.localId}`.
                """.trimIndent()
                )
            }
        }
    }
    command("list") {
        runs {
            val giveaways = getLabyrinthServer()!!.giveaways
                .filter { it.endTimeMillis >= System.currentTimeMillis() }
            if (giveaways.isEmpty())
                return@runs reply("${Emojis.X} **|** This server doesn't have any running giveaways").let {}
            reply(buildString {
                append("${Emojis.NOTEBOOK} **|** These are your active giveaways:")
                append("")
                giveaways.forEach {
                    append("**${it.localId}.** ${it.name} - ${it.endTimeMillis}")
                }
            })
        }
    }
    command("join") {
    }
    command("end") {

    }
}

private suspend fun startGiveaway(
    giveawayName: String,
    giveawayServer: LabyrinthServer,
    giveawayEndDate: Instant
): LabyrinthServerGiveaway {
    return newSuspendedTransaction {
        LabyrinthServerGiveaway.new {
            name = giveawayName
            server = giveawayServer
            endTimeMillis = giveawayEndDate.toEpochMilliseconds()
        }
    }
}