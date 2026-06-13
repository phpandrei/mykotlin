import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode

val itemRepository = ItemRepository()  // создаём экземпляр

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello from Kotlin via Docker! And Andrei с любовью!!!")
        }
        get("/ping") {
            val json = """{"status":"ok","time":${System.currentTimeMillis()}}"""
            call.respondText(json, ContentType.Application.Json)
        }
        get("/items") {
            val items = itemRepository.getAll()
            // формируем JSON
            val json = buildString {
                append("[")
                items.forEachIndexed { i, (id, name) ->
                    append("{\"id\":$id,\"name\":\"$name\"}")
                    if (i < items.size - 1) append(",")
                }
                append("]")
            }
            call.respondText(json, ContentType.Application.Json)
        }
        get("/mytable/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid id", status = HttpStatusCode.BadRequest)
                return@get
            }
            val item = itemRepository.getById(id)
            if (item == null) {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
                return@get
            }
            val (itemId, name) = item
            val json = "{\"id\":$itemId,\"name\":\"$name\"}"
            call.respondText(json, ContentType.Application.Json)
        }
    }
}