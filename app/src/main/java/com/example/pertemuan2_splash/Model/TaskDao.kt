package com.example.pertemuan2_splash.model

import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    suspend fun getActiveTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    suspend fun getCompletedTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE title LIKE :query")
    suspend fun searchTasksByTitle(query: String): List<Task>

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    suspend fun getTasksSortedByDueDateAscending(): List<Task>

    @Query("SELECT * FROM tasks ORDER BY dueDate DESC")
    suspend fun getTasksSortedByDueDateDescending(): List<Task>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}