package io.labyrinth.bot.guilded

import com.typesafe.config.ConfigFactory
import io.labyrinth.bot.guilded.util.LabyrinthConfig
import io.labyrinth.bot.guilded.util.inspectFile
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig

public suspend fun main() {
    val config: LabyrinthConfig = loadConfig()
    val labyrinth = LabyrinthBot(config)
    labyrinth.start()
}

@OptIn(ExperimentalSerializationApi::class)
private fun loadConfig(): LabyrinthConfig = inspectFile("config.conf").let {
    Hocon.decodeFromConfig(ConfigFactory.parseFile(it))
}