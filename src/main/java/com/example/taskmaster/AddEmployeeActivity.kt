// AddEmployeeActivity.kt
package com.example.taskmaster

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.taskmaster.databinding.ActivityAddEmployeeBinding

class AddEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEmployeeBinding
    private val viewModel: EmployeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_employee)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addEmployeeButton.setOnClickListener {
            addEmployee()
        }
    }

    private fun addEmployee() {
        val name = binding.empNameInput.text.toString().trim()
        val email = binding.empEmailInput.text.toString().trim()
        val password = binding.empPasswordInput.text.toString()

        when {
            name.isEmpty() -> {
                binding.empNameInput.error = "Name is required"
                binding.empNameInput.requestFocus()
                return
            }
            email.isEmpty() -> {
                binding.empEmailInput.error = "Email is required"
                binding.empEmailInput.requestFocus()
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.empEmailInput.error = "Please enter a valid email"
                binding.empEmailInput.requestFocus()
                return
            }
            password.length < 6 -> {
                binding.empPasswordInput.error = "Password must be at least 6 characters"
                binding.empPasswordInput.requestFocus()
                return
            }
        }

        // Clear any previous errors
        binding.empNameInput.error = null
        binding.empEmailInput.error = null
        binding.empPasswordInput.error = null

        viewModel.addEmployee(name, email, password)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.addEmployeeButton.isEnabled = !isLoading
            binding.addEmployeeButton.text = if (isLoading) "Adding..." else "Add Employee"
        }

        viewModel.addEmployeeResult.observe(this) { result ->
            result.onSuccess { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                clearForm()
                finish() // Go back to employees list
            }

            result.onFailure { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearForm() {
        binding.empNameInput.text?.clear()
        binding.empEmailInput.text?.clear()
        binding.empPasswordInput.text?.clear()
    }
}