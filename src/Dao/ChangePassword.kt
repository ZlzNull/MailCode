package com.zlz.Dao

import com.zlz.Bean.UserChangePassword
import com.zlz.Table.UserDataTable
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.update

fun changePassword(data:UserChangePassword){
    UserDataTable.update {
        it.userPassword to data.newPassword
        where { it.userQQ eq data.QQ }
    }
}