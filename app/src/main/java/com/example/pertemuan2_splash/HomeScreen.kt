package com.example.pertemuan2_splash

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pertemuan2_splash.model.AuthViewModel
import com.example.pertemuan2_splash.model.Task
import com.example.pertemuan2_splash.model.TaskViewModel
import com.example.pertemuan2_splash.model.TaskViewModelFactory
import java.util.Date
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(LocalContext.current.applicationContext as Application)
    )
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    val coroutineScope = rememberCoroutineScope()



    // Memuat semua task saat layar dibuka
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            taskViewModel.getAllTasks()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Task List",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Title TextField
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Description TextField
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Task Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Add/Update Task Button
        Button(
            onClick = {
                if (editingTask != null) {
                    // Update existing task
                    taskViewModel.updateTask(
                        editingTask!!.copy(
                            title = title,
                            description = description
                        )
                    )
                    editingTask = null
                } else {
                    // Add new task
                    taskViewModel.addTask(
                        title = title,
                        description = description,
                        dueDate = Date()
                    )
                }
                // Reset input fields
                title = ""
                description = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7218A5))
        ) {
            Text(
                text = if (editingTask != null) "Update Task" else "Add Task",
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Task List
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(tasks) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Task Details
                    Text(
                        text = "${task.title}: ${task.description}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    // Checkbox for task completion
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            taskViewModel.toggleTaskCompletion(task)
                        }
                    )

                    // Edit Button
                    IconButton(
                        onClick = {
                            // Populate fields for editing
                            editingTask = task
                            title = task.title
                            description = task.description
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = Color.Blue
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = {
                            taskViewModel.deleteTask(task)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        // Logout Button
        Button(
            onClick = {
                authViewModel.signout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7218A5)),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Logout",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}