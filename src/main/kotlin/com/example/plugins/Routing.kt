package com.example.plugins

import com.example.models.UserInfo
import com.example.models.UserResponse
import com.example.routing.authRoutes
import com.example.routing.notesRoutes
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.io.File

fun Application.configureRouting() {
    routing {
        // - [Get]
        get("/") {
            // * [Request]
            println("URI : ${call.request.uri}")
            // Headers
            println("Headers : ${call.request.headers.names()}")
            println("User-Agent : ${call.request.headers["User-Agent"]}")
            println("Accept : ${call.request.headers["Accept"]}")
            println("Query Params : ${call.request.queryParameters.names()}")
            // Query Parameters
            println("Name : ${call.request.queryParameters["name"]}")
            println("Email : ${call.request.queryParameters["email"]}")
            // call.respondText("Hello Ktor", status = HttpStatusCode.NoContent)
            // For Send Object Json in Response
            val responseObject = UserResponse("Mahmoud", "mahmoud@gmail.com")
            call.respond(responseObject)
        }
        get("/pageNumber/{page}") {
            val pageNumber = call.parameters["page"]
            call.respondText("Your are on Page Number : $pageNumber")
        }
        // For Add Headers
        get("/headers") {
            call.response.headers.append("server-name", "ktor-server")
            call.response.headers.append("coffee", "I love coffee")
            call.respondText("Headers Attached")
        }
        // For Response Downloading Image
        get("/fileDownload") {
            val file = File("files/a.jpg")
            call.response.header(
                HttpHeaders.ContentDisposition, ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName, "downloadableImage.jpg"
                ).toString()
            )
            call.respondFile(file)
        }
        // For Open File
        get("/fileOpen") {
            val file = File("files/b.jpg")
            call.response.header(
                HttpHeaders.ContentDisposition, ContentDisposition.Inline.withParameter(
                    ContentDisposition.Parameters.FileName, "openImage.jpg"
                ).toString()
            )
            call.respondFile(file)
        }
        // - [Post]
        post("/loginPage") {
            val userInfo = call.receive<UserInfo>()
            println("User Info : $userInfo")
            call.respondText("Login Page")
        }
    }
    notesRoutes()
    authRoutes()
}

