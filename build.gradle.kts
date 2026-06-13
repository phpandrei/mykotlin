plugins {
    kotlin("jvm") version "1.9.22"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-netty:2.3.7")

    // PostgreSQL драйвер
    implementation("org.postgresql:postgresql:42.7.1")

    // Exposed ORM (основные модули)
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.49.0")

    // HikariCP — пул соединений (рекомендуется с Exposed)
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Логгер (чтобы убрать SLF4J warning)
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

application {
    mainClass.set("ApplicationKt")
}

application {
    mainClass.set("ApplicationKt")
}