package io.labyrinth.bot.guilded.service

import com.deck.common.util.GenericId
import io.labyrinth.bot.guilded.database.entity.LabyrinthServer
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

public class ServerService {
    public suspend fun register(id: GenericId): LabyrinthServer = newSuspendedTransaction {
        search(id) ?: LabyrinthServer.new(id) {}
    }

    public suspend fun search(id: GenericId): LabyrinthServer? = newSuspendedTransaction {
        LabyrinthServer.findById(id)
    }

    public suspend fun delete(id: GenericId): Unit = newSuspendedTransaction {
        search(id)?.delete()
    }
}