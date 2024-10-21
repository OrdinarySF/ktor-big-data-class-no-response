package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*

sealed class R(val code: Int, val msg: String) {
    class Success<T>(code: Int, msg: String, val data: T?) : R(code, msg)

    companion object {
        fun <T> success(data: T? = null): R {
            return Success(200, "success", data)
        }
    }
}

suspend fun <T> ApplicationCall.success(data: T? = null) {
    respond(R.success(data))
}

data class Page<T>(val list: List<T>, val total: Long)

data class User(val name: String, val age: Int, val desc: String)
