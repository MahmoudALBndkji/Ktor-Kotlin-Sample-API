package com.example.routing

import com.example.db.DatabaseConnection
import com.example.entities.UserEntity
import com.example.models.NoteRequest
import com.example.models.NoteResponse
import com.example.models.User
import com.example.models.Users
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.mindrot.jbcrypt.BCrypt
import java.util.Locale

fun Application.authRoutes() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))
    routing {
        post("/register") {
            val user = call.receive<Users>()
            // Check For UserName & Password Length
            if (!user.validLength()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "UserName Should be >= 6 Characters & Password Should be >= 8 Characters"
                    ),
                )
                return@post
            }
            val username = user.username.lowercase(Locale.getDefault())
            val password = user.encryptingPassword()
            // Validation on User Exists
            val userValid = db.from(UserEntity)
                .select()
                .where(UserEntity.username eq username).map {
                    it[UserEntity.username]
                }.firstOrNull()
            if (userValid != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "User already exists"
                    ),
                )
                return@post
            }
            db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
            }
            call.respondText("Done")
        }
        post("/login") {
            val user = call.receive<Users>()
            if (!user.validLength()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "UserName Should be >= 6 Characters & Password Should be >= 8 Characters"
                    ),
                )
                return@post
            }
            val username = user.username.lowercase(Locale.getDefault())
            val password = user.password
            val userCheck = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val id = it[UserEntity.id]!!
                    val username = it[UserEntity.username]!!
                    val password = it[UserEntity.password]!!
                    User(id, username, password)
                }.firstOrNull()
            if (userCheck == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "Invalid UserName OR Password"
                    ),
                )
                return@post
            }
            val passwordMatch = BCrypt.checkpw(password, userCheck?.password)
            if (!passwordMatch) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    NoteResponse(
                        success = false,
                        data = "Invalid UserName OR Password"
                    ),
                )
                return@post
            }
            // Return Json Web Token
            val token = tokenManager.generateJWTToken(userCheck)
            // Login Successfully
            call.respond(
                HttpStatusCode.OK,
                NoteResponse(
                    success = true,
                    data = token,
                ),
            )
        }
    }
}