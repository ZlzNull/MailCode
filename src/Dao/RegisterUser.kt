package com.zlz.Dao

import com.zlz.Bean.UserData
import com.zlz.Table.UserDataTable
import me.liuwj.ktorm.dsl.insert
import java.util.*

val dbr = db

fun registerUser(userData: UserData) {
    UserDataTable.insert {
        it.userName to userData.userName
        it.userPassword to userData.userPassword
        it.userType to 1
        it.userQQ to userData.userQQ
        it.userStatus to true
        it.registerTime to Date().time
    }
}

