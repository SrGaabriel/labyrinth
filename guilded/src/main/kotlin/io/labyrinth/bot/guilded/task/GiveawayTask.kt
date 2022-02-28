package io.labyrinth.bot.guilded.task

import io.labyrinth.bot.guilded.LabyrinthBot
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerGiveaway
import io.labyrinth.bot.guilded.database.entity.LabyrinthServerGiveawaysTable
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

public class GiveawayTask(override val labyrinth: LabyrinthBot): LabyrinthTask {
    private var active: Boolean = false

    @OptIn(ObsoleteCoroutinesApi::class)
    override suspend fun init() {
        active = true
        val tickerChannel = ticker(10_000, 15_000)
        while (active) {
            tickerChannel.receive()
            val giveaways = newSuspendedTransaction {
                LabyrinthServerGiveaway.find {
                    LabyrinthServerGiveawaysTable.endTimeMillis lessEq System.currentTimeMillis()
                }
            }
            giveaways.forEach {
                labyrinth.giveawayService.endGiveaway(it)
            }
        }
    }

    override suspend fun kill() {
        active = false
    }
}