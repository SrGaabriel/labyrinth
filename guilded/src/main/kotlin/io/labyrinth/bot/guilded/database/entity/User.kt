package io.labyrinth.bot.guilded.database.entity

import com.deck.common.util.GenericId
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.util.Calendar

public class LabyrinthUser(id: EntityID<String>): Entity<String>(id) {
    public companion object: EntityClass<String, LabyrinthUser>(LabyrinthUserTable)

    public val userId: GenericId by id::value
    public var coins: Long by LabyrinthUserTable.coins
    public var daily: Long by LabyrinthUserTable.daily

    public fun isDailyAvailable(): Boolean {
        val tomorrowMillis = Calendar.getInstance().apply {
            timeInMillis = this@LabyrinthUser.daily
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        return System.currentTimeMillis() > tomorrowMillis
    }
}

public object LabyrinthUserTable: IdTable<String>("users") {
    override val id: Column<EntityID<String>> = varchar("id",8).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)

    public val coins: Column<Long> = long("coins").default(0)
    public val daily: Column<Long> = long("daily").default(0)
}