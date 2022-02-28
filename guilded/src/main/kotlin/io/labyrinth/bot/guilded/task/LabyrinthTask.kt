package io.labyrinth.bot.guilded.task

import io.labyrinth.bot.guilded.LabyrinthBot

public interface LabyrinthTask {
    public val labyrinth: LabyrinthBot

    public suspend fun init()

    public suspend fun kill()
}