package com.example.plugins

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.http.content.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    suspend fun bodyToString(subject: OutgoingContent): String = when (subject) {
        is TextContent -> subject.text
        is OutputStreamContent -> {
            val channel = ByteChannel(true)
            subject.writeTo(channel)
            val buffer = StringBuilder()
            while (!channel.isClosedForRead) {
                channel.readUTF8LineTo(buffer)
            }
            buffer.toString()
        }

        else -> String()
    }

    install(createApplicationPlugin("testPlugin") {
        on(ResponseBodyReadyForSend) { call, subject ->
            if (call.request.path() == "/json/jackson") {
                val bodyString = bodyToString(subject)
                //do something...
                println("body: $bodyString")
            }
        }
    })

    routing {
        fun testFunc(): Page<User> {
            val total = 10000
            val map = ArrayList<User>(total)
            for (i in 1..total) map.add(User("key$i", i, "desc$i"))
            return Page(map, total.toLong())
        }
        get("/json/jackson") {
            call.success(testFunc())
        }

        get("/json/noRead") {
            call.success(testFunc())
        }

    }

}
