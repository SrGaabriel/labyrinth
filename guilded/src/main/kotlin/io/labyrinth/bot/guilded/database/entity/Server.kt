package io.labyrinth.bot.guilded.database.entity

import com.deck.common.util.GenericId
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

public class LabyrinthServer(id: EntityID<String>): Entity<String>(id) {
    public companion object: EntityClass<String, LabyrinthServer>(LabyrinthServerTable)

    public val giveaways: SizedIterable<LabyrinthServerGiveaway> by LabyrinthServerGiveaway referrersOn LabyrinthServerGiveawaysTable.server

    public suspend fun getActiveGiveaways(): List<LabyrinthServerGiveaway> = newSuspendedTransaction {
        giveaways.filter {
            it.endTimeMillis >= System.currentTimeMillis()
        }
    }
}

public class LabyrinthServerGiveaway(id: EntityID<Int>): IntEntity(id) {
    public companion object: IntEntityClass<LabyrinthServerGiveaway>(LabyrinthServerGiveawaysTable)

    public var name: String by LabyrinthServerGiveawaysTable.name
    public var server: LabyrinthServer by LabyrinthServer referencedOn LabyrinthServerGiveawaysTable.server
    public var createdBy: String by LabyrinthServerGiveawaysTable.createdBy
    public var endTimeMillis: Long by LabyrinthServerGiveawaysTable.endTimeMillis
    public var participants: String by LabyrinthServerGiveawaysTable.participants

    public suspend fun getServer(): LabyrinthServer = newSuspendedTransaction { server }

    public fun getParticipants(): List<GenericId> =
        participants.split("|").filter { it.isNotEmpty() }
}

public object LabyrinthServerTable: IdTable<String>("servers") {
    override val id: Column<EntityID<String>> = varchar("id", 8).entityId()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public object LabyrinthServerGiveawaysTable: IntIdTable("giveaways", columnName = "global_id") {
    public val name: Column<String> = varchar("name", 32)
    public val server: Column<EntityID<String>> = reference("server", LabyrinthServerTable)
    public val createdBy: Column<String> = varchar("created_by", 8)
    public val endTimeMillis: Column<Long> = long("end_time")
    public val participants: Column<String> = varchar("participants", 65534).default("")
}