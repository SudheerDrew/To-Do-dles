package com.example.to_do_dles.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.to_do_dles.model.TaskList
import com.example.to_do_dles.model.ToDo

@Database(entities = [ToDo::class, TaskList::class], version = 1)
abstract class ToDoDatabase:RoomDatabase() {
        abstract fun  dao(): ToDoDao

        companion object{
            @Volatile private var instance : ToDoDatabase? = null

            private val lock = Any()

            operator fun invoke(context: Context) = instance ?: synchronized(lock){
                instance ?: createDatabase(context).also{
                    instance = it
                }
            }

            private fun createDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext, ToDoDatabase::class.java, "toDoDatabase"
            ).allowMainThreadQueries().build()

        }

}