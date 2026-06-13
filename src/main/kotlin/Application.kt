import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.ContentType

fun main() {
    // 1. Инициализируем БД (создаст таблицу и тестовые данные)
    configureDatabase()

    // 2. Запускаем сервер
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello from Kotlin via Docker!")
            }
            get("/ping") {
                val json = """{"status":"ok","time":${System.currentTimeMillis()}}"""
                call.respondText(json, ContentType.Application.Json)
            }
            get("/items") {
                val items = getAllItems()
                val json = buildString {
                    append("[")
                    items.forEachIndexed { index, (id, name) ->
                        append("{\"id\":$id,\"name\":\"$name\"}")
                        if (index < items.size - 1) append(",")
                    }
                    append("]")
                }
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}