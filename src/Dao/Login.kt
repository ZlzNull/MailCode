package com.zlz.Dao

import com.zlz.Table.UserDataTable
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.dsl.where

val dbl = db

fun searchUserByQQ(qq:String):Map<String,String>?{
    for(row in UserDataTable.select(UserDataTable.userPassword,UserDataTable.userQQ)){
        if (row[UserDataTable.userQQ] == qq){
            return mapOf("password" to row[UserDataTable.userPassword]!!,"name" to row[UserDataTable.userName]!!)
        }
    }
    return null
}