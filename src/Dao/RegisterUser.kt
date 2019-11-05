package com.zlz.Dao

import com.zlz.Bean.UserData
import com.zlz.Table.UserDataTable
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.schema.time
import java.util.*

val dba = db

fun registerUser(userData: UserData){
    UserDataTable.insert {
        it.userName to userData.userName
        it.userPassword to userData.userPassword
        it.userType to 1
        it.userQQ to userData.userQQ
        it.userStatus to true
        it.registerTime to Date().time
    }
}

