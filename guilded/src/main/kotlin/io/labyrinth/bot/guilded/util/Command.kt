package io.labyrinth.bot.guilded.util

import com.deck.extras.Emojis
import io.labyrinth.bot.guilded.command.LabyrinthCommandContext

public fun LabyrinthCommandContext.alphanumeric(text: String): String {
    if (!text.matches("[a-zA-Z0-9_ ]+".toRegex()))
        fail("This operation requires an alphanumeric string. Please try again removing special characters.", Emojis.X)
    return text
}