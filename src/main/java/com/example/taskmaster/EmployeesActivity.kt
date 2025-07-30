// EmployeesActivity.kt
package com.example.taskmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmaster.databinding.ActivityEmployeeBinding

class EmployeesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeBinding
    private val viewModel: EmployeeViewModel by viewModels()
    private lateinit var employeeAdapter: EmployeeAdapter

    private val addEmployeeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Refresh the list when returning from AddEmployeeActivity
        if (result.resultCode == RESULT_OK) {
            viewModel.loadEmployees()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_employee)

        setupRecyclerView()
        setupNavigation()
        observeViewModel()
        viewModel.loadEmployees()
    }

    private fun setupRecyclerView() {
        employeeAdapter = EmployeeAdapter { employee ->
            // Handle employee click - you can add actions like view details, edit, delete
            showEmployeeOptions(employee)
        }

        binding.employeeList.apply {
            layoutManager = LinearLayoutManager(this@EmployeesActivity)
            adapter = employeeAdapter
            // Remove the divider since we're using cards
            // addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupNavigation() {
        binding.addEmpButton.setOnClickListener {
            val intent = Intent(this, AddEmployeeActivity::class.java)
            addEmployeeLauncher.launch(intent)
        }

        binding.dashboardIcon.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        binding.tasksIcon.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
            finish()
        }

        binding.employeesIcon.setOnClickListener {
            // Already in employees - do nothing or highlight current tab
        }

        // Uncomment these when you have the respective activities
        // binding.reportsIcon.setOnClickListener {
        //     startActivity(Intent(this, ReportsActivity::class.java))
        //     finish()
        // }
        //
        // binding.settingsIcon.setOnClickListener {
        //     startActivity(Intent(this, SettingsActivity::class.java))
        //     finish()
        // }
        //
        // binding.notificationsIcon.setOnClickListener {
        //     startActivity(Intent(this, NotificationsActivity::class.java))
        //     finish()
        // }
        //
        // binding.profileIcon.setOnClickListener {
        //     startActivity(Intent(this, ProfileActivity::class.java))
        // }
    }

    private fun observeViewModel() {
        viewModel.employees.observe(this) { employees ->
            employeeAdapter.submitList(employees)

            // Show empty state if needed
            if (employees.isEmpty()) {
                // You can show an empty state view here
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide loading indicator if needed
        }
    }

    private fun showEmployeeOptions(employee: Employee) {
        // You can implement a bottom sheet or dialog with options like:
        // - View Details
        // - Edit Employee
        // - Delete Employee
        // - Assign Tasks

        Toast.makeText(this, "Clicked on ${employee.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        viewModel.loadEmployees()
    }
}