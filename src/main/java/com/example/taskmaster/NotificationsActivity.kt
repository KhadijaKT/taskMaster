package com.example.taskmaster

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.NotificationAdapter
import com.example.taskmaster.databinding.ActivityNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var database: DatabaseReference
    private lateinit var notificationAdapter: NotificationAdapter
    private val deleteRequests = mutableListOf<DeleteRequest>()
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        loadNotifications()
        setupNavigationClickListeners()
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter(deleteRequests) { deleteRequest ->
            showDeleteRequestDialog(deleteRequest)
        }

        binding.recyclerViewNotifications.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = notificationAdapter
        }
    }

    private fun loadNotifications() {
        // Show loading state if you have a loading indicator
        showLoadingState()

        database.child("deleteRequests")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    deleteRequests.clear()

                    for (deleteRequestSnapshot in snapshot.children) {
                        val deleteRequest = deleteRequestSnapshot.getValue(DeleteRequest::class.java)
                        deleteRequest?.let {
                            deleteRequests.add(it)
                        }
                    }

                    // Sort by most recent first
                    deleteRequests.sortByDescending { it.requestedAt }
                    notificationAdapter.notifyDataSetChanged()

                    hideLoadingState()

                    if (deleteRequests.isEmpty()) {
                        showEmptyState()
                    } else {
                        hideEmptyState()
                    }

                    Log.d("NotificationActivity", "Loaded ${deleteRequests.size} notifications")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("NotificationActivity", "Failed to load notifications: ${error.message}")
                    hideLoadingState()
                    Toast.makeText(
                        this@NotificationsActivity,
                        "Failed to load notifications: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showDeleteRequestDialog(deleteRequest: DeleteRequest) {
        // Get employee details first
        database.child("employees").child(deleteRequest.requestedById)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val employeeName = snapshot.child("name").getValue(String::class.java) ?: "Unknown Employee"
                    val employeeEmail = snapshot.child("email").getValue(String::class.java) ?: ""

                    showActionDialog(deleteRequest, employeeName, employeeEmail)
                }

                override fun onCancelled(error: DatabaseError) {
                    showActionDialog(deleteRequest, "Unknown Employee", "")
                }
            })
    }

    private fun showActionDialog(deleteRequest: DeleteRequest, employeeName: String, employeeEmail: String) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        val requestDate = dateFormat.format(Date(deleteRequest.requestedAt))

        val message = """
            Employee: $employeeName
            ${if (employeeEmail.isNotEmpty()) "Email: $employeeEmail\n" else ""}Task: ${deleteRequest.taskName}
            Reason: ${deleteRequest.reason}
            Requested: $requestDate
            
            What would you like to do?
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Delete Request")
            .setMessage(message)
            .setPositiveButton("Approve") { _, _ ->
                approveDeleteRequest(deleteRequest)
            }
            .setNegativeButton("Deny") { _, _ ->
                denyDeleteRequest(deleteRequest)
            }
            .setNeutralButton("View Details") { _, _ ->
                // You can navigate to a detailed view or show more info
                viewTaskDetails(deleteRequest.taskId)
            }
            .show()
    }

    private fun approveDeleteRequest(deleteRequest: DeleteRequest) {
        // Remove the task from tasks node
        database.child("tasks").child(deleteRequest.taskId).removeValue()
            .addOnSuccessListener {
                // Remove the delete request
                removeDeleteRequest(deleteRequest)
                Toast.makeText(this, "Task '${deleteRequest.taskName}' has been deleted", Toast.LENGTH_SHORT).show()

                // Optionally, log this action or notify the employee
                logAdminAction("APPROVED_DELETE", deleteRequest)
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to delete task: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun denyDeleteRequest(deleteRequest: DeleteRequest) {
        // Remove the delete request without deleting the task
        removeDeleteRequest(deleteRequest)
        Toast.makeText(this, "Delete request denied", Toast.LENGTH_SHORT).show()

        // Optionally, log this action or notify the employee
        logAdminAction("DENIED_DELETE", deleteRequest)
    }

    private fun removeDeleteRequest(deleteRequest: DeleteRequest) {
        database.child("deleteRequests")
            .orderByChild("taskId")
            .equalTo(deleteRequest.taskId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("NotificationActivity", "Failed to remove delete request: ${error.message}")
                }
            })
    }

    private fun viewTaskDetails(taskId: String) {
        database.child("tasks").child(taskId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskName = snapshot.child("name").getValue(String::class.java) ?: "Unknown Task"
                    val taskDescription = snapshot.child("description").getValue(String::class.java) ?: "No description"
                    val assignedTo = snapshot.child("assignedTo").getValue(String::class.java) ?: ""

                    val message = """
                        Task: $taskName
                        Description: $taskDescription
                        ${if (assignedTo.isNotEmpty()) "Assigned To: $assignedTo" else "Not assigned"}
                    """.trimIndent()

                    AlertDialog.Builder(this@NotificationsActivity)
                        .setTitle("Task Details")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@NotificationsActivity, "Failed to load task details", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun logAdminAction(action: String, deleteRequest: DeleteRequest) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val adminAction = mapOf(
            "action" to action,
            "taskId" to deleteRequest.taskId,
            "taskName" to deleteRequest.taskName,
            "requestedById" to deleteRequest.requestedById,
            "performedBy" to (currentUser?.uid ?: "unknown"),
            "performedAt" to System.currentTimeMillis()
        )

        database.child("adminActions").push().setValue(adminAction)
    }

    private fun showLoadingState() {
        isLoading = true
        // Show loading indicator if you have one
    }

    private fun hideLoadingState() {
        isLoading = false
        // Hide loading indicator if you have one
    }

    private fun showEmptyState() {
        // Show empty state view if you have one
        // For example: binding.emptyStateView.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        // Hide empty state view if you have one
        // For example: binding.emptyStateView.visibility = View.GONE
    }

    private fun setupNavigationClickListeners() {
        binding.dashboardIcon.setOnClickListener {
            startActivity(Intent(this,AdminDashboardActivity::class.java))
            finish()
        }

        binding.tasksIcon.setOnClickListener {
            startActivity(Intent(this,TasksActivity::class.java))
            finish()
        }

        binding.employeesIcon.setOnClickListener {
            startActivity(Intent(this,EmployeesActivity::class.java))
            finish()
        }

        binding.reportsIcon.setOnClickListener {
            // Navigate to Reports
        }

        binding.settingsIcon.setOnClickListener {
            // Navigate to Settings
        }

        binding.profileIcon.setOnClickListener {
            Toast.makeText(this@NotificationsActivity, "Already on Notification Screen,", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any listeners if needed
    }
}
