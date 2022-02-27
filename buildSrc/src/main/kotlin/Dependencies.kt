object Dependencies {
    const val KotlinVersion = "1.6.10"

    private const val KotlinxSerializationVersion = "1.3.2"
    const val KotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:$KotlinxSerializationVersion"
    const val KotlinxSerializationHocon = "org.jetbrains.kotlinx:kotlinx-serialization-hocon:$KotlinxSerializationVersion"

    private const val DeckBotVersion = "0.0.3-BOT"
    const val DeckBotCore = "com.github.srgaabriel.deck:bot-deck-core:$DeckBotVersion"
    const val DeckBotExtras = "com.github.srgaabriel.deck:bot-deck-extras:$DeckBotVersion"

    private const val ClubsVersion = "0.7-SNAPSHOT"
    const val ClubsBot = "com.github.srgaabriel.clubs:clubs-bot:$ClubsVersion"

    private const val ExposedVersion = "0.37.3"
    const val ExposedCore = "org.jetbrains.exposed:exposed-core:$ExposedVersion"
    const val ExposedDao = "org.jetbrains.exposed:exposed-dao:$ExposedVersion"
    const val ExposedJdbc ="org.jetbrains.exposed:exposed-jdbc:$ExposedVersion"

    const val HikariCp = "com.zaxxer:HikariCP:5.0.1"
    const val PostgreSql = "org.postgresql:postgresql:42.3.3"
}