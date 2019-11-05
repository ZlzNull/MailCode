package com.zlz.Table

import me.liuwj.ktorm.schema.*

object UserDataTable: Table<Nothing>("user_data") {
    val userID by int("user_id").primaryKey()
    val userName by varchar("user_name")
    val userPassword by varchar("user_password")
    val userType by int("user_type")
    val userQQ by varchar("user_qq").primaryKey()
    val userStatus by boolean("user_status")
    val registerTime by long("register_time")
}