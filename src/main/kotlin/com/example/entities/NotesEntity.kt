package com.example.entities

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object NotesEntity : Table<Nothing>(tableName = "note") {
    val id = int("id").primaryKey()
    val note = varchar("note")
}