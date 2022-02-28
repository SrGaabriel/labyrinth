package io.labyrinth.bot.guilded.service

import com.deck.common.util.GenericId
import com.deck.core.util.StatelessMessage
import com.deck.core.util.StatelessMessageChannel
import com.deck.core.util.StatelessServer
import com.deck.core.util.sendReply
import io.labyrinth.bot.guilded.LabyrinthBot
import io.labyrinth.bot.guilded.database.entity.LabyrinthServer
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerGiveaway
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

public class GiveawayService(public val labyrinth: LabyrinthBot) {
    public suspend fun startGiveaway(
        giveawayName: String,
        giveawayServer: LabyrinthServer,
        giveawayCreator: String,
        giveawayMessage: UUID,
        giveawayChannel: UUID,
        giveawayEndDate: Instant,
        giveawayWinnerCount: Int
    ): LabyrinthServerGiveaway {
        return newSuspendedTransaction {
            LabyrinthServerGiveaway.new {
                name = giveawayName
                server = giveawayServer
                channelId = giveawayChannel
                messageId = giveawayMessage
                createdBy = giveawayCreator
                winnerAmount = giveawayWinnerCount
                endTimeMillis = giveawayEndDate.toEpochMilliseconds()
            }
        }
    }

    public suspend fun endGiveaway(giveaway: LabyrinthServerGiveaway) {
        suspend fun deleteGiveaway() = newSuspendedTransaction { giveaway.delete() }
        val channel = StatelessMessageChannel(
            client = labyrinth.client,
            id = giveaway.channelId,
            server = StatelessServer(labyrinth.client, giveaway.getServer().id.value)
        )
        val message = StatelessMessage(
            client = labyrinth.client,
            id = giveaway.messageId,
            channel = channel
        )
        val winners = generateWinners(giveaway.winnerAmount, giveaway.getParticipants())
        if (winners.isEmpty()) {
            message.sendReply("""There were no participants in this giveaway, so it was canceled.""")
            return deleteGiveaway()
        }
        val endMessage = message.sendReply(buildString {
            appendLine(":tada: The giveaway has ended, and the winners are:")
            for (winner in winners) {
                appendLine("<@$winner>")
            }
        })
        message.update("_This giveaway has already ended, results are out [here](https://www.guilded.gg/teams/${channel.server?.id}/channels/${endMessage.channel.id}/chat?messageId=${endMessage.id})_.")
        deleteGiveaway()
    }

    private fun generateWinners(number: Int, participants: List<GenericId>): List<GenericId> = buildList {
        repeat(number) {
            participants.filter { it !in this }.randomOrNull() ?: return@buildList
        }
    }
}