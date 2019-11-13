package com.zlz.Intf

import com.zlz.Dao.db
import com.zlz.Table.UserDataTable
import io.ktor.http.content.PartData
import io.ktor.http.content.streamProvider
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.dsl.update
import me.liuwj.ktorm.dsl.where
import java.io.File

val dbu = db

fun saveUserAvatar(file: PartData.FileItem,qq:String) {
    File("resources/userAvatar/${file.originalFileName}").writeBytes(file.streamProvider.invoke().readBytes())
    UserDataTable.update {
        it.userAvatar to file.originalFileName
        where {
            it.userQQ eq qq
        }
    }
}

fun findUserAvatar(qq:String){
    UserDataTable.select(UserDataTable.userAvatar).where { UserDataTable.userQQ eq qq }
}