package io.labyrinth.bot.guilded.service

import com.deck.common.util.GenericId
import io.labyrinth.bot.guilded.database.entity.LabyrinthUser
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

public class UserService {
    public suspend fun register(id: GenericId): LabyrinthUser = newSuspendedTransaction {
        search(id) ?: LabyrinthUser.new(id) {}
    }

    public suspend fun search(id: GenericId): LabyrinthUser? = newSuspendedTransaction {
        LabyrinthUser.findById(id)
    }

    public suspend fun delete(id: GenericId): Unit = newSuspendedTransaction {
        search(id)?.delete()
    }
}