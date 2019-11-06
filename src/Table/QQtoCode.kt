package com.zlz.Table

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.varchar

//描述数据库表
object QQtoCode : Table<Nothing>("qq_to_code") {
    val qq by varchar("qq").primaryKey()
    val code by varchar("code")
    val time by long("time")
}