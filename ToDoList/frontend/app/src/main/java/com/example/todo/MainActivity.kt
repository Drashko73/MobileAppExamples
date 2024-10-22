package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo.model.ToDoTask
import com.example.todo.view_model.ToDoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApp()
        }
    }
}

@Composable
fun ToDoApp(viewModel: ToDoViewModel = viewModel()) {
    var newTaskName by remember { mutableStateOf("") }

    // Fetch tasks when the app starts
    LaunchedEffect(Unit) {
        viewModel.fetchTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ToDo List") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {

                // Input field for a new task
                BasicTextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
                                .padding(16.dp)
                        ) {
                            if (newTaskName.isEmpty()) {
                                Text("Enter new task name", color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                            }
                            innerTextField()
                        }
                    }
                )

                // Button to add the new task
                Button(
                    onClick = {
                        if (newTaskName.isNotEmpty()) {
                            val newTask = ToDoTask(0, newTaskName, false)
                            viewModel.createTask(newTask)
                            newTaskName = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Add Task")
                }

                // Loading spinner when fetching tasks
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    if (viewModel.taskList.value.isEmpty()) {
                        Text("No tasks available.")
                    } else {
                        Column {
                            viewModel.taskList.value.forEach { task ->
                                TaskItem(
                                    task = task,
                                    onDelete = { viewModel.deleteTask(it) },
                                    onToggleComplete = { viewModel.updateTask(it.copy(isCompleted = !it.isCompleted)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}



@Composable
fun TaskItem(task: ToDoTask, onDelete: (Int) -> Unit, onToggleComplete: (ToDoTask) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(task.activity, modifier = Modifier.weight(1f))
        Checkbox(checked = task.isCompleted, onCheckedChange = { onToggleComplete(task) })
        IconButton(onClick = { onDelete(task.id) }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoApp()
}
