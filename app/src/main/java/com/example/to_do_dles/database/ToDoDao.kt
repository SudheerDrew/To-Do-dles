package com.example.to_do_dles.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.to_do_dles.model.TaskList
import com.example.to_do_dles.model.ToDo

@Dao
interface ToDoDao {

    @Insert
    suspend fun insertTask(todo:ToDo):Long

    @Insert
    suspend fun  insertList(list : TaskList):Long

    @Query("SELECT * FROM todo WHERE uid= :uid")
    suspend fun getTask(uid: Int): ToDo

    @Query("SELECT * FROM todo WHERE requestCode= :requestCode")
    suspend fun getTaskByRequestCode(requestCode: Int): ToDo

    @Query("SELECT * FROM tasklist WHERE uid= :uid")
    suspend fun getTaskList(uid: Int): TaskList

    @Update
    suspend fun updateTask(task: ToDo)

    @Update
    suspend fun updateTaskList(taskList: TaskList)

    @Query("DELETE FROM ToDo WHERE uid= :uid")
    suspend fun deleteTask(uid:Int)

    @Query("DELETE FROM TaskList WHERE uid= :uid")
    suspend fun deleteTaskList(uid:Int)

    @Query("SELECT * FROM todo")
    suspend fun getAllTasks(): List<ToDo>

    @Query("SELECT * FROM tasklist")
    suspend fun getAllLists(): List<TaskList>

    @Query("DELETE FROM todo")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM TaskList")
    suspend fun deleteAllTaskLists()


    @Query("UPDATE TODO SET isCompleted= :isCompleted WHERE uid= :uid")
    suspend fun updateCompletion(uid:Int, isCompleted:Boolean)

    @Query("UPDATE TODO SET requestCode= -1 WHERE requestCode= :requestCode")
    suspend fun updateRequestCode(requestCode:Int)

    @Query("DELETE FROM todo WHERE listName= :listName")
    fun deleteAllTasksInsideList(listName:String)


}