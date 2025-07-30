package com.example.taskmaster

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var tasksListener: ValueEventListener? = null

    companion object {
        private const val TAG = "TaskViewModel"
    }

    init {
        loadTasks()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            try {
                database.child("employees").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = mutableListOf<Employee>()
                        for (child in snapshot.children) {
                            val employee = child.getValue(Employee::class.java)
                            if (employee != null) {
                                list.add(employee.copy(id = child.key ?: ""))
                            }
                        }
                        _employees.value = list
                        Log.d(TAG, "Loaded ${list.size} employees")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _error.value = "Failed to load employees: ${error.message}"
                        Log.e(TAG, "Error loading employees: ${error.message}")
                    }
                })
            } catch (e: Exception) {
                _error.value = "Exception loading employees: ${e.message}"
                Log.e(TAG, "Exception loading employees: ${e.message}")
            }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                val adminId = currentUser?.uid ?: ""
                val adminName = currentUser?.displayName ?: "Admin"

                val newTaskRef = database.child("tasks").push()
                val taskWithId = task.copy(
                    id = newTaskRef.key ?: "",
                    assignedById = adminId,
                    assignedByName = adminName,
                    createdAt = System.currentTimeMillis()
                )
                newTaskRef.setValue(taskWithId).await()
                Log.d(TAG, "Task created by $adminName: ${taskWithId.title}")
            } catch (e: Exception) {
                _error.value = "Failed to create task: ${e.message}"
                Log.e(TAG, "Error creating task: ${e.message}")
            }
        }
    }




    fun loadTasks() {
        Log.d(TAG, "Loading tasks...")

        tasksListener?.let {
            database.child("tasks").removeEventListener(it)
        }

        tasksListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (child in snapshot.children) {
                    val task = child.getValue(Task::class.java)
                    if (task != null) {
                        taskList.add(task.copy(id = child.key ?: ""))
                    }
                }
                _tasks.value = taskList
                Log.d(TAG, "Loaded ${taskList.size} tasks")
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = "Failed to load tasks: ${error.message}"
                Log.e(TAG, "Error loading tasks: ${error.message}")
            }
        }

        database.child("tasks").addValueEventListener(tasksListener!!)
    }

    fun updateTaskStatus(taskId: String, status: String) {
        viewModelScope.launch {
            try {
                database.child("tasks").child(taskId).child("status").setValue(status).await()
            } catch (e: Exception) {
                _error.value = "Failed to update status: ${e.message}"
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                database.child("tasks").child(taskId).removeValue().await()
            } catch (e: Exception) {
                _error.value = "Failed to delete task: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tasksListener?.let {
            database.child("tasks").removeEventListener(it)
        }
    }
}
