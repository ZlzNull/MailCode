package com.zlz

import com.fasterxml.jackson.databind.SerializationFeature
import com.google.gson.Gson
import com.zlz.Bean.*
import com.zlz.Dao.changePassword
import com.zlz.Dao.equalCode
import com.zlz.Dao.registerUser
import com.zlz.Dao.sendCode
import com.zlz.Intf.login
import com.zlz.Table.QQtoCode
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.jackson.jackson
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.options
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.sessions
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.dsl.where
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Sessions) {
        cookie<MySession>("MySESSION")
        cookie<User>("USER")
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    val client = HttpClient(Apache) {
    }

    routing {
        get("/loginInformation") {
            try {
                val loginInformation = call.sessions.get("USER")
                println((loginInformation as User).userName)
                call.respond(mapOf("code" to 200))
            } catch (e: TypeCastException) {
                call.respond(mapOf("code" to 400))
            }
        }

        get("/logout") {
            call.sessions.clear("USER")
            call.respond(mapOf("code" to 200))
        }

        post("/login") {
            val data = Gson().fromJson(call.receiveText(), UserLogin::class.java)
            println(data.toString())
            val map = login(data)
            println(map.toString())
            if (map["code"] == 200) {
                call.sessions.set("USER", User(data.qq, map["name"]!! as String))
                map.remove("name")
            }
            call.respond(map)
        }

        post("/register") {
            val qq = (call.sessions.get("MySESSION") as MySession).qq
            println("register -- $qq")
            val data = Gson().fromJson(call.receiveText(), UserNameAndCodeAndPassword::class.java)
            val map = equalCode(data.code, qq)
            if (map["code"] == 200) {
                registerUser(UserData(data.userName, qq, data.userPassword))
                call.sessions.clear("MySESSION")
            }
            call.respond(map)
        }

        post("/changePassword") {
            val data = Gson().fromJson(call.receiveText(), UserChangePassword::class.java)
            println("changePassword -- ${data.QQ}")
            val map = equalCode(data.code, data.QQ)
            if (map["code"] == 200) {
                changePassword(data)
            }
            call.respond(map)
        }


        post("/MailCode") {
            val data = Gson().fromJson(call.receiveText(), UserQQ::class.java)
            println("MailCode -- ${data.QQ}")
            if (data.type) {
                call.sessions.set("MySESSION", MySession(data.QQ))
            }
            call.respond(sendCode(data))
        }

        get("/{type}/{name}") {
            val type = call.parameters["type"] ?: ""
            val name = call.parameters["name"] ?: ""
            call.respondFile(File("resources/${type}/${name}"))
        }
    }
}

data class MySession(val qq: String)
data class User(
    val qq: String,
    val userName: String
)

