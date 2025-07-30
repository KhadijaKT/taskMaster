package com.example.taskmaster

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.taskmaster.databinding.ActivtyAssignTaskBinding

class AssignTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivtyAssignTaskBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var employeeAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activty_assign_task)
        binding.lifecycleOwner = this

        viewModel.loadEmployees()

        setupBackButton()
        setupSpinner()
        setupCreateButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupSpinner() {
        employeeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        employeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.employeeSpinner.adapter = employeeAdapter

        viewModel.employees.observe(this) { employees ->
            val names = employees.map { it.name }
            employeeAdapter.clear()
            employeeAdapter.addAll(names)
            employeeAdapter.notifyDataSetChanged()
        }
    }

    private fun setupCreateButton() {
        binding.createTaskButton.setOnClickListener {
            val title = binding.taskNameInput.text.toString().trim()
            val description = binding.taskDetailsInput.text.toString().trim()
            val selectedPosition = binding.employeeSpinner.selectedItemPosition

            if (validateInput(title, description, selectedPosition)) {
                viewModel.employees.value?.let { employees ->
                    val selectedEmployee = employees[selectedPosition]
                    val task = Task(
                        title = title,
                        description = description,
                        assignedTo = selectedEmployee.id,
                        assignedToName = selectedEmployee.name
                    )
                    viewModel.createTask(task)
                    Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun validateInput(title: String, description: String, position: Int): Boolean {
        return when {
            title.isEmpty() -> {
                binding.taskNameInput.error = "Task name required"
                false
            }
            description.isEmpty() -> {
                binding.taskDetailsInput.error = "Task details required"
                false
            }
            position == AdapterView.INVALID_POSITION -> {
                Toast.makeText(this, "Please select an employee", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}