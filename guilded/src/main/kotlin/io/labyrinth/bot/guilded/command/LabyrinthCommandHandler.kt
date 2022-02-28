package io.labyrinth.bot.guilded.command

import com.deck.core.event.message.DeckMessageCreateEvent
import dev.gaabriel.clubs.bot.impl.BotCommandContext
import dev.gaabriel.clubs.common.handler.CommandHandler
import dev.gaabriel.clubs.common.parser.ArgumentParser
import dev.gaabriel.clubs.common.struct.Command
import dev.gaabriel.clubs.common.util.CommandCall
import dev.gaabriel.clubs.common.util.FailedCommandExecutionException
import dev.gaabriel.clubs.common.util.FailureHandler
import io.labyrinth.bot.guilded.LabyrinthBot
import io.labyrinth.bot.guilded.util.InternalFailureException

public class LabyrinthCommandHandler(
    public val labyrinth: LabyrinthBot,
    public val failureHandler: FailureHandler<*>,
    public val argumentParser: ArgumentParser<BotCommandContext>
): CommandHandler<DeckMessageCreateEvent> {
    @Suppress("unchecked_cast")
    override suspend fun execute(command: CommandCall<*>, event: DeckMessageCreateEvent) {
        val declaration =
            command.command as Command<LabyrinthCommandContext>; failureHandler as FailureHandler<BotCommandContext>
        val context = LabyrinthCommandContext(
            labyrinth = labyrinth,
            client = event.client,
            user = event.author,
            server = event.server,
            channel = event.channel,
            message = event.message,
            command = declaration,
            rawArguments = command.arguments
        )
        try {
            context._arguments = argumentParser.parseArguments(context, command.arguments)
            declaration.call(context)
        } catch (exception: InternalFailureException) {
            context.send(exception.message!!)
        } catch (exception: FailedCommandExecutionException) {
            failureHandler.onFailure(context, exception.failure)
        } catch (throwable: Throwable) {
            context.fancy("Unexpected error occurred: `${throwable::class.qualifiedName}: ${throwable.message}`")
            throwable.printStackTrace()
        }
    }
}