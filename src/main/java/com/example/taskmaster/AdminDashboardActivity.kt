package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.databinding.AdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: AdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of setting counts dynamically (these would typically come from Firebase or a database)
        binding.tasksCount.text = "24"
        binding.completedTasksCount.text = "12"
        binding.pendingTasksCount.text = "8"
        binding.deletedTasksCount.text = "4"

        // Example click listener for dashboard icons
        binding.dashboardIcon.setOnClickListener {
            Toast.makeText(this, "Already on Dashboard", Toast.LENGTH_SHORT).show()
        }

        binding.tasksIcon.setOnClickListener {
            Toast.makeText(this, "Tasks clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, TasksActivity::class.java))
        }

        binding.employeesIcon.setOnClickListener {
            Toast.makeText(this, "Employees clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, EmployeesActivity::class.java))
        }

        binding.reportsIcon.setOnClickListener {
            Toast.makeText(this, "Reports clicked", Toast.LENGTH_SHORT).show()
        }

        binding.settingsIcon.setOnClickListener {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        }

        binding.notificationsIcon.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.profileIcon.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
        }

        // Cards can also be interactive
        binding.tasksCard.setOnClickListener {
            Toast.makeText(this, "View all tasks", Toast.LENGTH_SHORT).show()
        }

        binding.completedTasksCard.setOnClickListener {
            Toast.makeText(this, "View completed tasks", Toast.LENGTH_SHORT).show()
        }

        binding.pendingTasksCard.setOnClickListener {
            Toast.makeText(this, "View pending tasks", Toast.LENGTH_SHORT).show()
        }

        binding.deletedTasksCard.setOnClickListener {
            Toast.makeText(this, "View deleted tasks", Toast.LENGTH_SHORT).show()
        }
    }
}
