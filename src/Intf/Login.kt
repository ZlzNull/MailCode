package com.zlz.Intf

import com.zlz.Bean.UserLogin
import com.zlz.Dao.searchUserByQQ

fun login(data: UserLogin): MutableMap<String, Any> =
    when (val map = searchUserByQQ(data.qq)) {
        null -> {
            mutableMapOf("code" to 400, "msg" to "该QQ号未注册")
        }
        else -> {
            if (map["password"] == data.password) {
                mutableMapOf("code" to 200, "name" to map["name"]!!)
            } else {
                mutableMapOf("code" to 400, "msg" to "密码错误")
            }
        }
    }

