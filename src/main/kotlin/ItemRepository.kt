import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ItemRepository {
    fun getAll(): List<Pair<Int, String>> = transaction {
        MyTable.selectAll().map { row ->
            row[MyTable.id] to row[MyTable.name]
        }
    }

    fun getById(id: Int): Pair<Int, String>? = transaction {
        MyTable.select { MyTable.id eq id }
            .singleOrNull()
            ?.let { row -> row[MyTable.id] to row[MyTable.name] }
    }

    // Для полноты можно добавить create, update, delete
}