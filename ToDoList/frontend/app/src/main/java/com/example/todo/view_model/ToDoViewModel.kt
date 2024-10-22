package com.example.todo.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.RetrofitConfig
import com.example.todo.model.ToDoTask
import kotlinx.coroutines.launch
import retrofit2.Response

class ToDoViewModel : ViewModel() {

    var taskList: List<ToDoTask> = listOf()
    var isLoading = true

    // Fetch all tasks
    fun fetchTasks() {
        viewModelScope.launch {
            val response = RetrofitConfig.toDoTaskService.getTasks()
            if (response.isSuccessful) {
                taskList = response.body() ?: listOf()
                isLoading = false
            }
        }
    }

    // Create a new task
    fun createTask(task: ToDoTask) {
        viewModelScope.launch {
            RetrofitConfig.toDoTaskService.createTask(task)
            fetchTasks()  // Refresh task list after creating a new one
        }
    }

    // Update a task
    fun updateTask(task: ToDoTask) {
        viewModelScope.launch {
            RetrofitConfig.toDoTaskService.updateTask(task)
            fetchTasks()  // Refresh task list after update
        }
    }

    // Delete a task
    fun deleteTask(id: Int) {
        viewModelScope.launch {
            RetrofitConfig.toDoTaskService.deleteTask(id)
            fetchTasks()  // Refresh task list after deletion
        }
    }
}
