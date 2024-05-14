package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.contactUsModule() {
    routing {
        get("/contactus") {
            call.respondText("Contact Us")
        }
        get("/about") {
            call.respondText("About Us")
        }
    }
}