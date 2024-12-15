package com.example.pertemuan2_splash.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = DatabaseProvider.getDatabase(application)
    private val taskDao = database.taskDao()

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _activeTasks = MutableLiveData<List<Task>>()
    val activeTasks: LiveData<List<Task>> = _activeTasks

    private val _completedTasks = MutableLiveData<List<Task>>()
    val completedTasks: LiveData<List<Task>> = _completedTasks

    // Status loading dan error handling
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Inisialisasi untuk memuat task saat ViewModel dibuat
    init {
        loadTasks()
    }

    // Metode loadTasks yang akan memuat semua task
    fun loadTasks() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _tasks.value = taskDao.getAllTasks()
                _activeTasks.value = taskDao.getActiveTasks()
                _completedTasks.value = taskDao.getCompletedTasks()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load tasks: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTask(title: String, description: String, dueDate: Date? = null) {
        // Validasi input
        if (title.isBlank()) {
            _errorMessage.value = "Title cannot be empty"
            return
        }

        _isLoading.value = true
        val task = Task(
            title = title,
            description = description,
            dueDate = dueDate
        )
        viewModelScope.launch {
            try {
                taskDao.insert(task)
                loadTasks() // Memuat ulang task setelah menambahkan
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add task: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(task: Task) {
        // Validasi input
        if (task.title.isBlank()) {
            _errorMessage.value = "Title cannot be empty"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                taskDao.update(task)
                loadTasks() // Memuat ulang task setelah memperbarui
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(task: Task) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                taskDao.delete(task)
                loadTasks() // Memuat ulang task setelah menghapus
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(isCompleted = !task.isCompleted)
                taskDao.update(updatedTask)
                loadTasks() // Memuat ulang task setelah mengubah status
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to toggle task completion: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fungsi pencarian task
    fun searchTasksByTitle(query: String) {
        viewModelScope.launch {
            try {
                _tasks.value = taskDao.searchTasksByTitle("%$query%")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to search tasks: ${e.localizedMessage}"
            }
        }
    }

    // Fungsi pengurutan task
    fun sortTasksByDueDate(ascending: Boolean = true) {
        viewModelScope.launch {
            try {
                _tasks.value = if (ascending) {
                    taskDao.getTasksSortedByDueDateAscending()
                } else {
                    taskDao.getTasksSortedByDueDateDescending()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to sort tasks: ${e.localizedMessage}"
            }
        }
    }

    // Fungsi untuk membersihkan error message
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Reset loading state
    fun resetLoadingState() {
        _isLoading.value = false
    }

    fun getAllTasks() {
        viewModelScope.launch {
            try {
                _tasks.value = taskDao.getAllTasks()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}