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
    embeddedServer(Netty, port = 8080, module = Application::configureRouting).start(wait = true)
}