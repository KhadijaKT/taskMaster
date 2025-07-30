//package com.example.taskmaster
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.taskmaster.databinding.ActivityEmpTasksBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//
//class EmployeeTasks : AppCompatActivity() {
//
//    private lateinit var binding: ActivityEmpTasksBinding
//    private lateinit var adapter: EmpTaskAdapter
//    private val taskList = mutableListOf<Task>()
//    private val auth = FirebaseAuth.getInstance()
//    private val TAG = "EmployeeTasks"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "onCreate called")
//
//        binding = ActivityEmpTasksBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Check if user is logged in
//        val currentUser = auth.currentUser
//        if (currentUser == null) {
//            Log.e(TAG, "No user logged in!")
//            showToast("Please log in first")
//            finish()
//            return
//        }
//
//        Log.d(TAG, "Current user: ${currentUser.uid}")
//        Log.d(TAG, "Current user email: ${currentUser.email}")
//
//        adapter = EmpTaskAdapter(taskList) { task ->
//            val intent = Intent(this, DeleteRequestActivity::class.java)
//            intent.putExtra("taskId", task.id)
//            intent.putExtra("taskName", task.title)
//            startActivity(intent)
//        }
//
//        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
//        binding.recyclerViewTasks.adapter = adapter
//
//        setupNavigationMenu()
//        loadEmployeeTasks()
//    }
//
//    private fun setupNavigationMenu() {
//        binding.dashboardIcon.setOnClickListener {
//            showToast("Dashboard clicked")
//            startActivity(Intent(this, EmployeeDashboardActivity::class.java))
//        }
//        binding.tasksIcon.setOnClickListener {
//            showToast("Tasks clicked")
//        }
//        binding.settingsIcon.setOnClickListener {
//            showToast("Settings clicked")
//        }
//        binding.notificationsIcon.setOnClickListener {
//            showToast("Notifications clicked")
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun loadEmployeeTasks() {
//        val currentUserId = auth.currentUser?.uid
//        if (currentUserId == null) {
//            showToast("User not authenticated")
//            return
//        }
//
//        // Show current user ID in a toast for debugging
//        showToast("Current User ID: $currentUserId")
//
//        val tasksRef = FirebaseDatabase.getInstance().getReference("tasks")
//
//        tasksRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                taskList.clear()
//
//                if (!snapshot.exists()) {
//                    showToast("No tasks node found in database")
//                    return
//                }
//
//                var debugInfo = "Database tasks:\n"
//                var foundMatch = false
//
//                for (taskSnap in snapshot.children) {
//                    val task = taskSnap.getValue(Task::class.java)
//                    if (task != null) {
//                        debugInfo += "Task: ${task.title}\n"
//                        debugInfo += "AssignedTo: '${task.assignedTo}'\n"
//                        debugInfo += "Current: '$currentUserId'\n"
//                        debugInfo += "Match: ${task.assignedTo == currentUserId}\n\n"
//
//                        // Try multiple comparison methods
//                        if (task.assignedTo == currentUserId ||
//                            task.assignedTo.trim() == currentUserId.trim() ||
//                            task.assignedTo.equals(currentUserId, ignoreCase = true)) {
//                            taskList.add(task)
//                            foundMatch = true
//                        }
//                    }
//                }
//
//                // Show debug info in a long toast (for debugging only)
//                Toast.makeText(this@EmployeeTasks, debugInfo, Toast.LENGTH_LONG).show()
//
//                adapter.notifyDataSetChanged()
//
//                if (foundMatch) {
//                    showToast("Loaded ${taskList.size} tasks")
//                } else {
//                    showToast("No matching tasks found")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                showToast("Failed to load tasks: ${error.message}")
//            }
//        })
//    }
//}

package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.databinding.ActivityEmpTasksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EmployeeTasks : AppCompatActivity() {

    private lateinit var binding: ActivityEmpTasksBinding
    private lateinit var adapter: EmpTaskAdapter
    private val taskList = mutableListOf<Task>()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "EmployeeTasks"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        binding = ActivityEmpTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "No user logged in!")
            showToast("Please log in first")
            finish()
            return
        }

        Log.d(TAG, "Current user: ${currentUser.uid}")
        Log.d(TAG, "Current user email: ${currentUser.email}")

        adapter = EmpTaskAdapter(taskList) { task ->
            val intent = Intent(this, DeleteRequestActivity::class.java)
            intent.putExtra("taskId", task.id)
            intent.putExtra("taskName", task.title)
            startActivity(intent)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = adapter

        setupNavigationMenu()
        loadEmployeeTasks()
    }

    private fun setupNavigationMenu() {
        binding.dashboardIcon.setOnClickListener {
            showToast("Dashboard clicked")
            startActivity(Intent(this, EmployeeDashboardActivity::class.java))
        }
        binding.tasksIcon.setOnClickListener {
            showToast("Tasks clicked")
        }
        binding.settingsIcon.setOnClickListener {
            showToast("Settings clicked")
        }
        binding.notificationsIcon.setOnClickListener {
            showToast("Notifications clicked")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadEmployeeTasks() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showToast("User not authenticated")
            return
        }

        val currentUserEmail = currentUser.email
        if (currentUserEmail == null) {
            showToast("User email not found")
            return
        }

        // First, find the employee ID by matching the email
        val employeesRef = FirebaseDatabase.getInstance().getReference("employees")

        employeesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(employeeSnapshot: DataSnapshot) {
                var employeeId: String? = null

                // Find employee by email
                for (empSnap in employeeSnapshot.children) {
                    val employee = empSnap.getValue(Employee::class.java)
                    if (employee != null && employee.email == currentUserEmail) {
                        employeeId = employee.id
                        break
                    }
                }

                if (employeeId == null) {
                    showToast("Employee profile not found")
                    return
                }

                // Now load tasks assigned to this employee ID
                loadTasksForEmployee(employeeId)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load employee data: ${error.message}")
            }
        })
    }

    private fun loadTasksForEmployee(employeeId: String) {
        val tasksRef = FirebaseDatabase.getInstance().getReference("tasks")

        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()

                if (!snapshot.exists()) {
                    showToast("No tasks found")
                    return
                }

                for (taskSnap in snapshot.children) {
                    val task = taskSnap.getValue(Task::class.java)
                    if (task != null && task.assignedTo == employeeId) {
                        taskList.add(task)
                    }
                }

                adapter.notifyDataSetChanged()
                showToast("Loaded ${taskList.size} tasks")
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load tasks: ${error.message}")
            }
        })
    }
}
