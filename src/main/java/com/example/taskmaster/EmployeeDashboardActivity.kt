package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaster.R
import com.example.taskmaster.databinding.EmployeeDashboardBinding

class EmployeeDashboardActivity : AppCompatActivity() {

    private lateinit var binding: EmployeeDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EmployeeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationMenu()
        setupCardClicks()
    }

    private fun setupNavigationMenu() {
        binding.dashboardIcon.setOnClickListener {
            showToast("Dashboard clicked")
        }
        binding.tasksIcon.setOnClickListener {
            showToast("Tasks clicked")
            startActivity(Intent(this, EmployeeTasks::class.java))
        }
        binding.settingsIcon.setOnClickListener {
            showToast("Settings clicked")
        }
        binding.notificationsIcon.setOnClickListener {
            showToast("Notifications clicked")
        }
    }

    private fun setupCardClicks() {
        binding.employeeTasksCard.setOnClickListener {
            showToast("Total Tasks Card clicked")
        }
        binding.employeeCompletedTasksCard.setOnClickListener {
            showToast("Completed Tasks Card clicked")
        }
        binding.employeePendingTasksCard.setOnClickListener {
            showToast("Pending Tasks Card clicked")
        }
        binding.employeeDeletedTasksCard.setOnClickListener {
            showToast("Deleted Tasks Card clicked")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
