package com.example.routing

import com.example.db.DatabaseConnection
import com.example.entities.NotesEntity
import com.example.models.NoteRequest
import com.example.models.NoteResponse
import com.example.models.Notes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where

fun Application.notesRoutes() {
    val db = DatabaseConnection.database
    routing {
        // End Point For Get Data From MySql Database
        get("/notes") {
            val notes = db.from(NotesEntity).select().map {
                val id = it[NotesEntity.id]
                val note = it[NotesEntity.note]
                Notes(id ?: -1, note ?: "")
            }
            call.respond(notes)
        }
        // End Point For Get By Id From MySql Database
        get("/notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val note = db.from(NotesEntity).select()
                .where(NotesEntity.id eq id)
                .map {
                    val id = it[NotesEntity.id]!!
                    val note = it[NotesEntity.note]!!
                    Notes(id = id, note = note)
                }.firstOrNull()
            if (note == null) {
                call.respond(
                    HttpStatusCode.NotFound, NoteResponse(
                        success = false,
                        data = "Could not found note with Id : $id"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK, NoteResponse(
                        success = true,
                        data = note,
                    )
                )
            }
        }
        // End Point For Insert Data To MySql Database
        post("/notes") {
            val request = call.receive<NoteRequest>()
            val result = db.insert(NotesEntity) {
                set(it.note, request.note)
            }
            if (result == 1) {
                call.respond(
                    HttpStatusCode.OK, NoteResponse(
                        success = true,
                        data = "Values has been Successfully inserted"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, NoteResponse(
                        success = false,
                        data = "Failed to insert values"
                    )
                )
            }
        }
        // End Point For Update Data From MySql Database
        put("/notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val updateNote = call.receive<NoteRequest>()
            val rowsEffected = db.update(NotesEntity) {
                set(it.note, updateNote.note)
                where {
                    it.id eq id
                }
            }
            if (rowsEffected == 1) {
                call.respond(
                    HttpStatusCode.OK, NoteResponse(
                        success = true,
                        data = "Note has been updated",
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, NoteResponse(
                        success = false,
                        data = "Failed to updated"
                    )
                )
            }
        }
        // End Point For Delete Data From MySql Database
        delete("/notes/{id}") {
            val id = call.parameters["id"]?.toInt() ?: -1
            val rowsEffected = db.delete(NotesEntity) {
                it.id eq id
            }
            if (rowsEffected == 1) {
                call.respond(
                    HttpStatusCode.OK, NoteResponse(
                        success = true,
                        data = "Note has been deleted",
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, NoteResponse(
                        success = false,
                        data = "Failed to deleted"
                    )
                )
            }
        }
    }
}