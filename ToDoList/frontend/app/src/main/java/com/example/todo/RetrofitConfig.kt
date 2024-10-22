package com.example.todo


import com.example.todo.service.ToDoTaskService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//paralelno je kontroleru sa beka, ovde se kreiraju servisi koji salju requestove
object RetrofitConfig {

    val toDoTaskService: ToDoTaskService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5168/api/ToDo/")  // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ToDoTaskService::class.java)
    }
}
