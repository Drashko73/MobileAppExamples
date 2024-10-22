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
        content = { paddingValues ->  // Accept the paddingValues parameter
            Column(
                modifier = Modifier
                    .fillMaxSize() // Fill the screen vertically
                    .padding(paddingValues) // Apply the inner padding from Scaffold (to avoid overlap with app bar)
                    .padding(16.dp) // Additional padding for the content
            ) {

                // Input field for a new task
                BasicTextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    modifier = Modifier
                        .fillMaxWidth() // Input field will take up full width
                        .padding(vertical = 8.dp), // Padding between input field and surrounding content
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth() // Box takes full width
                                .background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
                                .padding(16.dp) // Padding inside the box for better text field layout
                        ) {
                            if (newTaskName.isEmpty()) {
                                Text("Enter new task name", color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                            }
                            innerTextField() // Display the actual input field
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
                    modifier = Modifier.padding(top = 8.dp) // Space above the button
                ) {
                    Text("Add Task")
                }

                // Task list
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    Column {
                        viewModel.taskList.forEach { task ->
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
