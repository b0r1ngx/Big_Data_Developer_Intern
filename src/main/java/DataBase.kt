import org.sql2o.Sql2o
import java.net.URI
import java.net.URISyntaxException

fun dB(): MutableList<Int> {
        var sql2o: Sql2o? = null
            try {
                val dbUri = if (System.getenv("DATABASE_URL") == null) {
                    URI("postgres://localhost:5432/to_do")
                } else {
                    URI(System.getenv("DATABASE_URL"))
                }
                val port = dbUri.port
                val host = dbUri.host
                val path = dbUri.path
                val username =
                    if (dbUri.userInfo == null) null else dbUri.userInfo.split(":".toRegex()).toTypedArray()[0]
                println(username)
                val password =
                    if (dbUri.userInfo == null) null else dbUri.userInfo.split(":".toRegex()).toTypedArray()[1]
                println(password)
                sql2o = Sql2o("jdbc:postgresql://$host:$port$path", username, password)
            } catch (e: URISyntaxException) {
                println("Unable to connect to database.")
            }
    sql2o = Sql2o("jdbc:hsqldb:mem:testDB", "postgres", "")
    val request = "SELECT limit_value FROM limits_per_hour"
    sql2o!!.open().use { con -> return con.createQuery(request).executeScalarList(Int::class.java) }
}


fun main() {
    dB()
}