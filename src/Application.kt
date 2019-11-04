package com.zlz

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import com.fasterxml.jackson.databind.*
import com.google.gson.Gson
import com.zlz.Bean.UserData
import com.zlz.Bean.UserLogin
import com.zlz.Bean.UserNameAndCodeAndPassword
import com.zlz.Bean.UserQQ
import com.zlz.Dao.equalCode
import com.zlz.Dao.registerUser
import com.zlz.Dao.sendCode
import com.zlz.Intf.login
import com.zlz.Table.QQtoCode
import io.ktor.jackson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.request.receiveText
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.sessions
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update
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

    val client = HttpClient(Apache) {
    }

    routing {
        get("/loginInformation"){
            try {
                val loginInformation = call.sessions.get("USER")
                println((loginInformation as User).userName)
            }catch (e: Exception){
                println(e.toString())
            }
            call.respond(mapOf("code" to 200))
        }

        post("/login"){
            val data = Gson().fromJson(call.receiveText(), UserLogin::class.java)
            val map = login(data)
            call.sessions.set("USER",User(data.qq,map["name"]!! as String))
            map.remove("name")
            call.respond(map)
        }

        post("/register") {
            val qq = (call.sessions.get("MySESSION") as MySession).qq
            println("register -- $qq")
            val data = Gson().fromJson(call.receiveText(), UserNameAndCodeAndPassword::class.java)
            val map = equalCode(data.code, qq)
            if (map["code"] == 200) {
                registerUser(UserData(data.userName, qq, data.userPassword))
            }
            call.respond(map)
        }

        post("/MailCode") {
            val qq = Gson().fromJson(call.receiveText(), UserQQ::class.java)
            println("MailCode -- ${qq.QQ}")
            if (qq.type) {
                call.sessions.set("MySESSION", MySession(qq.QQ))
            }
            call.respond(sendCode(qq))
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

