package com.example.models

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class Users(
    val username: String,
    val password: String,
) {
    fun encryptingPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun validLength(): Boolean {
        return username.length >= 6 && password.length >= 8
    }
}
