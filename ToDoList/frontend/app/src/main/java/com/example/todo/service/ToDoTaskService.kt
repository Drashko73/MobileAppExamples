package com.example.todo.service

import com.example.todo.model.ToDoTask
import retrofit2.Response
import retrofit2.http.*

interface ToDoTaskService {

    @GET("GetAllItems")
    suspend fun getTasks(): Response<List<ToDoTask>>

    @POST("CreateItem")
    suspend fun createTask(@Body task: ToDoTask): Response<ToDoTask>

    @PUT("UpdateItem")
    suspend fun updateTask(@Body task: ToDoTask): Response<ToDoTask>

    @DELETE("DeleteItem/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<Void>
}