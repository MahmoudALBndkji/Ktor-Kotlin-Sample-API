package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.db.DatabaseConnection.database
import com.example.entities.NotesEntity
import com.example.plugins.*
import com.example.routing.notesRoutes
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import java.nio.file.Paths.get
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import io.ktor.server.config.HoconApplicationConfig

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // For Enable Returning Json Data
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
    contactUsModule()
    // Calling JWT Configuration
    val config = environment.config
    install(Authentication) {
        jwt {
            val jwtAudience = config.property("jwt.audience").getString()
            realm = config.property("jwt.realm").getString()
            verifier(
                JWT.require(Algorithm.HMAC256(config.property("jwt.secret").getString()))
                    .withAudience(jwtAudience)
                    .withIssuer(config.property("jwt.issuer").getString())
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    // - For Insert To MySql Database
    database.insert(NotesEntity) {
        set(it.note, "Hello Kotlin")
    }
    // - For Read Data from MySql Database
    val notes = database.from(NotesEntity).select()
    for (row in notes) {
        println(" ${row[NotesEntity.id]} : ${row[NotesEntity.note]}")
    }
    // - For Update Data from MySql Database
    database.update(NotesEntity) {
        set(it.note, "Hi Kotlin")
        where {
            it.id eq 1
        }
    }
    // - For Delete Data from MySql Database
    database.delete(NotesEntity) {
        it.id eq 5
    }
}

