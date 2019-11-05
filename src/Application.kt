package com.zlz

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import com.fasterxml.jackson.databind.*
import com.google.gson.Gson
import com.zlz.Bean.*
import com.zlz.Dao.changePassword
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
import io.ktor.sessions.clear
import io.ktor.sessions.cookie
import io.ktor.sessions.sessions
import kotlinx.coroutines.plus
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.dsl.update
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

    val client = HttpClient(Apache) {
    }

    routing {
        get("/loginInformation") {
            call.response.header("Access-Control-Allow-Origin","*")
            try {
                val loginInformation = call.sessions.get("USER")
                println((loginInformation as User).userName)
                call.respond(mapOf("code" to 200))
            } catch (e: TypeCastException) {
                call.respond(mapOf("code" to 400))
            }
        }

        get("/logout"){
            call.response.header("Access-Control-Allow-Origin","*")
            call.sessions.clear("USER")
            call.respond(mapOf("code" to 200))
        }

        post("/login") {
            call.response.header("Access-Control-Allow-Origin","*")
            val data = Gson().fromJson(call.receiveText(), UserLogin::class.java)
            println(data.toString())
            val map = login(data)
            println(map.toString())
            if(map["code"] == 200){
                call.sessions.set("USER", User(data.qq, map["name"]!! as String))
                map.remove("name")
            }
            call.respond(map)
        }

        post("/register") {
            call.response.header("Access-Control-Allow-Origin","*")
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

        post("/changePassword"){
            call.response.header("Access-Control-Allow-Origin","*")
            val data = Gson().fromJson(call.receiveText(), UserChangePassword::class.java)
            println("changePassword -- ${data.QQ}")
            val map = equalCode(data.code,data.QQ)
            if(map["code"] == 200){
                changePassword(data)
            }
            call.respond(map)
        }

        get("/test"){
            call.response.header("Access-Control-Allow-Origin","*")
            val db = Database.connect(
                "jdbc:mysql://127.0.0.1:3306/ktorm?serverTimezone=UTC",
                "com.mysql.cj.jdbc.Driver",
                "root",
                "root"
            )
            val data = QQtoCode.select(QQtoCode.code,QQtoCode.time).where { QQtoCode.qq eq "100" }
            data.forEach {
                println(it.size())
            }
        }

        post("/MailCode") {
            call.response.header("Access-Control-Allow-Origin","*")
            val data = Gson().fromJson(call.receiveText(), UserQQ::class.java)
            println("MailCode -- ${data.QQ}")
            if (data.type) {
                call.sessions.set("MySESSION", MySession(data.QQ))
            }
            call.respond(sendCode(data))
        }

        get("/{type}/{name}") {
            call.response.header("Access-Control-Allow-Origin","*")
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

