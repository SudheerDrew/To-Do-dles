package com.example.to_do_dles.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList(
    @ColumnInfo
    var name:String,
    @ColumnInfo
    var color: Int ){
    @PrimaryKey(autoGenerate = true)
    var uid = 0
}