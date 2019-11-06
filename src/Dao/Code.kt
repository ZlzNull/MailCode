package com.zlz.Dao

import com.zlz.Bean.UserQQ
import com.zlz.Intf.sendMail
import com.zlz.Table.QQtoCode
import com.zlz.Table.UserDataTable
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import java.sql.SQLIntegrityConstraintViolationException
import java.util.*

val db = Database.connect(
    "jdbc:mysql://123.56.224.188:3306/ktorm?serverTimezone=UTC",
    "com.mysql.cj.jdbc.Driver",
    "root",
    "Whyzshngrd1@db"
)

fun saveCode(qq: String, code: String) {
    QQtoCode.update {
        it.code to code
        it.time to Date().time
        where { it.qq eq qq }
    }
}

fun equalCode(code: String, qq: String): Map<String, Any> {
    val data = QQtoCode.select(QQtoCode.code, QQtoCode.time).where { QQtoCode.qq eq qq }
    data.forEach {
        return if (it[QQtoCode.code] == code) {
            if (it[QQtoCode.time]!! - Date().time < 120000) {
                mapOf("code" to 200)
            } else {
                mapOf("code" to 400, "msg" to "验证码已失效，请重新发送验证码")
            }
        } else {
            mapOf("code" to 400, "msg" to "验证码错误")
        }

    }
    return mapOf("code" to 400, "msg" to "请求出错了，请稍后再试")
}

fun sendCode(qq: UserQQ): Map<String, Any> {
    return if (qq.type) {
        for (row in UserDataTable.select(UserDataTable.userQQ).where { UserDataTable.userQQ eq qq.QQ }) {
            return mapOf("code" to 400, "msg" to "该QQ已被注册，请尝试登陆或找回密码")
        }
        try {
            QQtoCode.insert {
                it.qq to qq.QQ
                it.code to ""
                it.time to 0
            }
        } catch (e: SQLIntegrityConstraintViolationException) {

        }
        mail(qq)
    } else {
        mail(qq)
    }
}

fun mail(qq: UserQQ): Map<String, Any> =
    try {
        sendMail(qq.QQ, qq.type)
        mapOf("code" to 200)
    } catch (e: Exception) {
        println(e.toString())
        mapOf("code" to 400, "msg" to "未知错误，请稍后再试")
    }
