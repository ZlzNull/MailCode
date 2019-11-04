package com.zlz.Table

import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.varchar

object UserDataTable: Table<Nothing>("user_data") {
    val userQQ by varchar("user_qq").primaryKey()
    val userName by varchar("user_name")
    val userPassword by varchar("user_password")
}