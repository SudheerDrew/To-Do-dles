package com.example.to_do_dles.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.to_do_dles.enums.Priority
import com.example.to_do_dles.enums.Repeat

@Entity
data class ToDo(
    @ColumnInfo
    var task:String,
    @ColumnInfo
    var priority: Priority = Priority.LOW,
    @ColumnInfo
    var isCompleted: Boolean = false,
    @ColumnInfo
    var listName:String = "All",
    @ColumnInfo
    var remindTimeInMillis : Long = 0,
    @ColumnInfo
    var repeat: Repeat = Repeat.NOT,
    @ColumnInfo
    var requestCode: Int = -1,
    @Embedded
    var tracker:Tracker = Tracker()){


    @PrimaryKey(autoGenerate = true)
    var uid:Int = 0

    @ColumnInfo
    var description = ""
}