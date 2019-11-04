package com.zlz.Dao

import com.zlz.Bean.UserData
import com.zlz.Table.UserDataTable
import me.liuwj.ktorm.dsl.*

val dba = db

fun registerUser(userData: UserData){
    UserDataTable.insert {
        it.userName to userData.userName
        it.userPassword to userData.userPassword
        it.userQQ to userData.userQQ
    }
}

