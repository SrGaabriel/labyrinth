plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Dependencies.KotlinxSerializationJson)
    implementation(Dependencies.KotlinxSerializationHocon)

    implementation(Dependencies.DeckBotCore)
    implementation(Dependencies.DeckBotExtras)

    implementation(Dependencies.ClubsBot)

    implementation(Dependencies.ExposedCore)
    implementation(Dependencies.ExposedDao)
    implementation(Dependencies.ExposedJdbc)

    implementation(Dependencies.HikariCp)
    runtimeOnly(Dependencies.PostgreSql)
}