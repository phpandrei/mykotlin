import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.ResultRow

object MyTable : Table("mytable") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

fun configureDatabase() {
    val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://postgres:5432/mydb"
    val dbUser = System.getenv("DB_USER") ?: "myuser"
    val dbPassword = System.getenv("DB_PASSWORD") ?: "mypassword"

    val config = HikariConfig().apply {
        jdbcUrl = dbUrl
        username = dbUser
        password = dbPassword
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 3
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(MyTable)
        // теперь selectAll доступен
        if (MyTable.selectAll().empty()) {
            MyTable.insert { it[name] = "Item 1" }
            MyTable.insert { it[name] = "Item 2" }
            MyTable.insert { it[name] = "Kotlin rocks" }
        }
    }
}