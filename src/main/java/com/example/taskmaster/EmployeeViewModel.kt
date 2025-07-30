// EmployeeViewModel.kt
package com.example.taskmaster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel() {
    private val repository = EmployeeRepository()

    val employees: LiveData<List<Employee>> = repository.employees
    val addEmployeeResult: LiveData<Result<String>> = repository.addEmployeeResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadEmployees() {
        // Employees are loaded automatically through repository's ValueEventListener
        // This method exists for explicit loading if needed
    }

    fun addEmployee(name: String, email: String, password: String) {
        if (!validateEmployeeData(name, email, password)) {
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val employee = Employee(
                name = name.trim(),
                email = email.trim(),
                password = password
            )

            val result = repository.addEmployee(employee)
            _isLoading.value = false

            // The result is automatically posted through repository's LiveData
        }
    }

    fun updateEmployee(employee: Employee) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.updateEmployee(employee)
            _isLoading.value = false
        }
    }

    fun deleteEmployee(employeeId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.deleteEmployee(employeeId)
            _isLoading.value = false
        }
    }

    private fun validateEmployeeData(name: String, email: String, password: String): Boolean {
        return when {
            name.isBlank() -> {
                // Handle validation error - name is required
                false
            }
            email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                // Handle validation error - valid email is required
                false
            }
            password.length < 6 -> {
                // Handle validation error - password too short
                false
            }
            else -> true
        }
    }
}