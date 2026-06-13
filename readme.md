markdown
# Kotlin / Ktor : полный построчный справочник (сравнение с PHP)

Этот файл объясняет **каждую строку** вашего проекта. Открываете его — и видите, за что отвечает любая деталь, с примерами из PHP.

## Содержание
- [build.gradle.kts](#buildgradlekts)
- [settings.gradle.kts](#settingsgradlekts)
- [src/main/kotlin/Application.kt](#srcmainkotlinapplicationkt)
- [Dockerfile](#dockerfile)
- [docker-compose.yml](#docker-composeyml)

---

## build.gradle.kts

```kotlin
// 1. Плагины Gradle – расширяют возможности сборки
plugins {
    // Плагин Kotlin для JVM: компилирует .kt в байт-код Java
    // Аналог в PHP: указание версии PHP в composer.json + расширение "ext-*"
    kotlin("jvm") version "1.9.22"
    
    // Плагин "application" – добавляет команды run и installDist (создаёт готовый дистрибутив)
    // Аналог в PHP: написание консольного скрипта в composer.json (scripts)
    application
}

// 2. Репозитории – откуда скачивать библиотеки
repositories {
    // Maven Central – главный склад библиотек для JVM (как Packagist для PHP)
    mavenCentral()
}

// 3. Зависимости – что нужно подключить к проекту
dependencies {
    // Встроенный веб-сервер Netty (Ktor)
    // Аналог в PHP: Apache / nginx + PHP-FPM, но здесь сервер внутри приложения
    implementation("io.ktor:ktor-server-netty:2.3.7")
    
    // Умеет договариваться о формате данных (Content-Type)
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    
    // Превращает Kotlin-объекты в JSON и обратно
    // Аналог в PHP: json_encode / json_decode
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
}

// 4. Настройка плагина application
application {
    // Указываем класс, в котором находится функция main()
    // Имя ApplicationKt автоматически генерируется из имени файла Application.kt
    // Аналог в PHP: точка входа – обычно index.php или имя скрипта в CLI
    mainClass.set("ApplicationKt")
}
settings.gradle.kts
kotlin
// Имя проекта. Используется для именования папки со сборкой (build/install/<имя>)
// Аналог в PHP: "name" в composer.json (только для метаданных)
rootProject.name = "kotlin-first"
src/main/kotlin/Application.kt
kotlin
// Импорты – подключаем нужные классы/функции (как use в PHP)
import io.ktor.server.engine.*   // для embeddedServer
import io.ktor.server.netty.*    // серверный движок Netty
import io.ktor.server.response.* // respond, respondText
import io.ktor.server.routing.*  // routing, get, post...
import io.ktor.server.application.* // Application, call

// Точка входа в программу (как <?php в скрипте или function main() в CLI)
fun main() {
    // Создаём встроенный веб-сервер на движке Netty, порт 8080 (внутри контейнера)
    // Аналог в PHP: php -S 0.0.0.0:8080, только здесь сервер полноценный
    embeddedServer(Netty, port = 8080) {
        // Блок, где описываем маршруты (роутинг)
        routing {
            // Обработчик GET-запроса на путь "/"
            // Аналог в PHP: if ($_SERVER['REQUEST_URI'] == '/') { ... }
            get("/") {
                // Отправляем простой текстовый ответ (Content-Type: text/plain)
                // Аналог в PHP: echo "Hello...";
                call.respondText("Hello from Kotlin via Docker!")
            }

            // Обработчик GET /ping
            get("/ping") {
                // mapOf – создаёт неизменяемую карту (ключ → значение)
                // Аналог в PHP: ['status' => 'ok', 'time' => microtime(true)]
                val data = mapOf(
                    "status" to "ok",
                    "time" to System.currentTimeMillis()
                )
                // respond автоматически превращает объект в JSON
                // Аналог в PHP: header('Content-Type: application/json'); echo json_encode($data);
                call.respond(data)
            }
        }
    }.start(wait = true)   // Запускаем сервер и блокируем поток (ждём запросы)
}
Dockerfile
dockerfile
# ---------- Этап 1: builder (сборка) ----------
# Базовый образ: Gradle 8.5 с JDK 17 (нужен для компиляции и сборки)
FROM gradle:8.5-jdk17 AS builder

# Рабочая директория внутри контейнера (аналог cd /app)
WORKDIR /app

# Копируем всё из текущей папки (где лежит Dockerfile) в /app
COPY . .

# Запускаем Gradle: собрать дистрибутив приложения (скомпилировать Kotlin, скачать зависимости, упаковать)
# Аналог в PHP: composer install --no-dev && ... (но здесь ещё компиляция)
RUN gradle installDist

# ---------- Этап 2: финальный образ (только для запуска) ----------
# Базовый образ: минимальный OpenJDK 17 (без Gradle, чтобы образ был маленьким)
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем собранный дистрибутив из первого этапа в текущую папку
# build/install/kotlin-first содержит папки bin/ (скрипты) и lib/ (все jar)
COPY --from=builder /app/build/install/kotlin-first .

# Декларируем порт, который будет слушать контейнер (документация, реальной публикации не делает)
EXPOSE 8080

# Команда по умолчанию при запуске контейнера – запустить скрипт приложения
# Аналог в PHP: CMD ["php", "-S", "0.0.0.0:8080"]
CMD ["./bin/kotlin-first"]
docker-compose.yml
yaml
# Версия формата docker-compose (3.8 – современная)
version: '3.8'

# Список сервисов (контейнеров). Здесь один сервис.
services:
  # Имя сервиса (будет использоваться как имя контейнера)
  kotlin-app:
    # Собирать образ из Dockerfile, который лежит в текущей папке (.)
    build: .
    # Проброс портов: "порт_на_хосте : порт_в_контейнере"
    # Хост будет слушать 8081, контейнер слушает 8080 (из Application.kt)
    # Если хотите 8080 на хосте – меняйте на "8080:8080"
    ports:
      - "8081:8080"
    # Монтируем папку src с хоста в контейнер (удобно для разработки)
    # При изменении кода на хосте – они видны внутри, но для перезапуска надо пересобрать или использовать dev-режим
    volumes:
      - ./src:/app/src
Быстрая шпаргалка: PHP → Kotlin (веб)
Задача	PHP	Kotlin (этот проект)
Старт сервера	php -S 0.0.0.0:8080 + Apache/nginx	fun main() запускает embeddedServer
Простой текст	echo "Hello";	call.respondText("Hello")
JSON-ответ	json_encode($arr)	call.respond(mapOf(...))
GET /ping	if ($_SERVER['REQUEST_URI'] == '/ping')	get("/ping") { ... }
Null-безопасность	$x = $obj?->property ?? 'default'	val len = text?.length ?: 0
Зависимости	composer require vendor/package	implementation("group:artifact:version")
Контейнер	FROM php:8.2-apache	многоступенчатый gradle + openjdk
Этот файл можно держать открытым на второй монитор. Любая строчка вашего проекта здесь пояснена. Если что-то забыли – ищите по ключевым словам.
