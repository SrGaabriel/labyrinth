package io.labyrinth.bot.guilded.command.misc

import com.deck.core.util.sendMessage
import com.deck.extras.Emojis
import com.deck.extras.conversation.type.conversate
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.integer
import io.labyrinth.bot.guilded.command.LabyrinthCommandContext
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerGiveaway
import io.labyrinth.bot.guilded.service.CommandService
import io.labyrinth.bot.guilded.util.alphanumeric
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.time.Duration.Companion.seconds

public val CommandService.giveaway: Command<LabyrinthCommandContext> get() = command("giveaway") {
    val giveawayService = labyrinth.giveawayService
    command("start") {
        runs {
            user.conversate(channel) {
                val name = alphanumeric((ask("${Emojis.MEMO} **|** What's the giveaway name?")
                    .awaitReply(60_000)
                    ?: return@conversate timeout())
                    .content
                )
                if (name.length > 32)
                    return@conversate run {
                        channel.sendMessage("${Emojis.X} **|** The giveaway name can't have more than 32 characters.")
                        end()
                    }
                val description = alphanumeric((ask("${Emojis.MEMO} **|** What's the giveaway description?")
                    .awaitReply(90_000)
                    ?: return@conversate timeout())
                    .content
                )
                val endDate = Clock.System.now() + (30).seconds
                val giveaway = giveawayService.startGiveaway(
                    giveawayName = name,
                    giveawayServer = getLabyrinthServer(),
                    giveawayCreator = user.id,
                    giveawayMessage = message.id,
                    giveawayChannel = channel.id,
                    giveawayEndDate = endDate,
                    giveawayWinnerCount = 1
                )
                val giveawayMessage = channel.sendMessage {
                    content = """
                    __**$name | GIVEAWAY #${giveaway.id.value}**__
                    
                    ${Emojis.NOTEBOOK} **Description**: `$description`
                    
                    To participate you just need to execute `${CommandService.PREFIX}giveaway join ${giveaway.id.value}`.
                """.trimIndent()
                }
                newSuspendedTransaction {
                    giveaway.messageId = giveawayMessage.id
                }
            }
        }
    }
    command("list") {
        runs {
            val giveaways = getLabyrinthServer().getActiveGiveaways()
            if (giveaways.isEmpty())
                return@runs reply("${Emojis.X} **|** This server doesn't have any active giveaways at the moment").let {}
            reply(buildString {
                appendLine("${Emojis.NOTEBOOK} **|** These are the currently active giveaways in this server:")
                giveaways.forEachIndexed { index, giveaway ->
                    appendLine("**${index}. ${giveaway.name.uppercase()}** - `${giveaway.endTimeMillis}`")
                }
            })
        }
    }
    command("join") {
        val giveawayId by integer("giveaway_id")
        runs {
            val giveaway = newSuspendedTransaction {
                LabyrinthServerGiveaway.findById(giveawayId)
            } ?: return@runs fancy("The mentioned giveaway couldn't be found. Make sure that you've typed the giveaway id correctly and try again.", Emojis.X).let {}
            if (giveaway.getServer().id.value != server?.id)
                return@runs fancy("The mentioned giveaway is not happening on this server. To participate in a giveaway, you must use the join command in the giveaway's respective server.", Emojis.X).let {}
            val participants = giveaway.getParticipants()
            if (user.id in participants)
                return@runs fancy("You are already participating in this giveaway. If you wish to back away, use `${CommandService.PREFIX}giveaway leave $giveawayId`.").let {}
            newSuspendedTransaction {
                giveaway.participants += "${user.id}|"
            }
            fancy("You are now participating in this giveaway, good luck! If you wish to stop concurring, you can execute `${CommandService.PREFIX}giveaway leave $giveawayId`.", Emojis.ADMISSION_TICKETS).let {}
        }
    }
    command("leave") {
        val giveawayId by integer("giveaway_id")
        runs {
            val giveaway = newSuspendedTransaction {
                LabyrinthServerGiveaway.findById(giveawayId)
            } ?: return@runs fancy("The mentioned giveaway couldn't be found. Make sure that you've typed the giveaway id correctly and try again.", Emojis.X).let {}
            if (giveaway.getServer().id.value != server?.id)
                return@runs fancy("The mentioned giveaway is not happening on this server. To leave a giveaway, you must use the leave command in the giveaway's respective server.", Emojis.X).let {}
            val participants = giveaway.getParticipants()
            if (user.id !in participants)
                return@runs fancy("You are not participating in this giveaway. If you wish to enter, use `${CommandService.PREFIX}giveaway join $giveawayId`.").let {}
            newSuspendedTransaction {
                giveaway.participants = giveaway.participants.replace("${user.id}|", "")
            }
            fancy("You left this giveaway, what a shame! If you change your mind, use `${CommandService.PREFIX}giveaway join $giveawayId` to join again.", Emojis.CLOSED_UMBRELLA).let {}
        }
    }
    command("end") {
        val giveawayId by integer("giveaway_id")
        runs {
            val giveaway = newSuspendedTransaction {
                LabyrinthServerGiveaway.findById(giveawayId)
            } ?: return@runs fancy(
                "The mentioned giveaway couldn't be found. Make sure that you've typed the giveaway id correctly and try again.",
                Emojis.X
            ).let {}
            if (giveaway.getServer().id.value != server?.id)
                return@runs fancy(
                    "The mentioned giveaway is not happening on this server. To end a giveaway, you must use the end command in the giveaway's respective server.",
                    Emojis.X
                ).let {}
            if (giveaway.createdBy != user.id)
                return@runs fancy("You can not end this giveaway, given you're not its creator.", Emojis.X).let {}
            giveawayService.endGiveaway(giveaway)
        }
    }
}