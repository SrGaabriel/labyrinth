package io.labyrinth.bot.guilded.database.entity

import com.deck.common.util.GenericId
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

public class LabyrinthUser(id: EntityID<String>): Entity<String>(id) {
    public companion object: EntityClass<String, LabyrinthUser>(LabyrinthUserTable)

    public val userId: GenericId by id::value
    public var coins: Long by LabyrinthUserTable.coins
}

public object LabyrinthUserTable: IdTable<String>("users") {
    override val id: Column<EntityID<String>> = varchar("id",8).entityId()

    public val coins: Column<Long> = long("coins").default(0)
}