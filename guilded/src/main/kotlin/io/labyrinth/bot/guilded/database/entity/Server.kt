package io.labyrinth.bot.guilded.database.entity

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SizedIterable
import java.util.UUID

public class LabyrinthServer(id: EntityID<String>): Entity<String>(id) {
    public companion object: EntityClass<String, LabyrinthServer>(LabyrinthServerTable)

    public val giveaways: SizedIterable<LabyrinthServerGiveaway> by LabyrinthServerGiveaway referrersOn LabyrinthServerGiveawaysTable.server
}

public class LabyrinthServerGiveaway(id: EntityID<UUID>): UUIDEntity(id) {
    public companion object: UUIDEntityClass<LabyrinthServerGiveaway>(LabyrinthServerGiveawaysTable)

    public var name: String by LabyrinthServerGiveawaysTable.name
    public var server: LabyrinthServer by LabyrinthServer referencedOn LabyrinthServerGiveawaysTable.server
    public var localId: Int by LabyrinthServerGiveawaysTable.localId
    public var endTimeMillis: Long by LabyrinthServerGiveawaysTable.endTimeMillis
    public var participants: String by LabyrinthServerGiveawaysTable.participants
}

public object LabyrinthServerTable: IdTable<String>("servers") {
    override val id: Column<EntityID<String>> = varchar("id", 8).entityId()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

public object LabyrinthServerGiveawaysTable: UUIDTable("giveaways", columnName = "global_id") {
    public val name: Column<String> = varchar("name", 32)
    public val server: Column<EntityID<String>> = reference("server", LabyrinthServerTable)
    public val localId: Column<Int> = integer("local_id").autoIncrement()
    public val endTimeMillis: Column<Long> = long("end_time")
    public val participants: Column<String> = varchar("participants", 65534).default("")
}