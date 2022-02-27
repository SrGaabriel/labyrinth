package io.labyrinth.bot.guilded.command

import com.deck.core.DeckClient
import com.deck.core.entity.Message
import com.deck.core.stateless.StatelessServer
import com.deck.core.stateless.StatelessUser
import com.deck.core.stateless.channel.StatelessMessageChannel
import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.common.struct.Command
import io.labyrinth.bot.guilded.LabyrinthBot
import io.labyrinth.bot.guilded.database.entity.LabyrinthServer
import io.labyrinth.bot.guilded.database.entity.LabyrinthUser

@Suppress("unchecked_cast")
public class LabyrinthCommandContext(
    public val labyrinth: LabyrinthBot,
    client: DeckClient,
    user: StatelessUser,
    server: StatelessServer?,
    channel: StatelessMessageChannel,
    message: Message,
    command: Command<LabyrinthCommandContext>,
    rawArguments: List<String>,
): BotCommandContext(client, user, server, channel, message, command as Command<BotCommandContext>, rawArguments) {
    private var labyrinthUser: LabyrinthUser? = null
    private var labyrinthServer: LabyrinthServer? = null

    internal var _arguments: List<Any> = listOf()
    override val arguments: List<Any> get() = _arguments

    public suspend fun getLabyrinthUser(): LabyrinthUser {
        if (labyrinthUser == null)
            labyrinthUser = labyrinth.userService.register(user.id)
        return labyrinthUser!!
    }

    public suspend fun getLabyrinthServer(): LabyrinthServer? {
        if (server == null) return null
        if (labyrinthServer == null)
            labyrinthServer = labyrinth.serverService.register(user.id)
        return labyrinthServer
    }
}