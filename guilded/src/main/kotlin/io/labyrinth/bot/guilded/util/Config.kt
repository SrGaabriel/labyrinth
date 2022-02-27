package io.labyrinth.bot.guilded.util

import io.labyrinth.bot.guilded.LabyrinthBot
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
public data class LabyrinthConfig(
    val token: String,
    val database: LabyrinthDatabaseConfig
)

@Serializable
public data class LabyrinthDatabaseConfig(
    val host: String,
    val port: String,
    val username: String,
    val password: String,
    val database: String
)

public fun inspectFile(fileName: String, createIfMissing: Boolean = true): File {
    val file = File(fileName)
    if (file.exists().not()) {
        if (createIfMissing) {
            file.createNewFile()
            file.writeBytes(LabyrinthBot::class.java.getResourceAsStream("/$fileName")!!.readAllBytes())
        }
        error("File $fileName was required for booting but was not found.")
    }
    return file
}