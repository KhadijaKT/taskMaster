// EmployeeRepository.kt
package com.example.taskmaster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class EmployeeRepository {
    private val database = FirebaseDatabase.getInstance()
    private val employeesRef = database.getReference("employees")

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _addEmployeeResult = MutableLiveData<Result<String>>()
    val addEmployeeResult: LiveData<Result<String>> = _addEmployeeResult

    init {
        // Listen for real-time updates
        employeesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val employeeList = mutableListOf<Employee>()
                snapshot.children.forEach { child ->
                    child.getValue(Employee::class.java)?.let { employee ->
                        employeeList.add(employee)
                    }
                }
                _employees.value = employeeList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    suspend fun addEmployee(employee: Employee): Result<String> {
        return try {
            val employeeId = employeesRef.push().key ?: throw Exception("Failed to generate employee ID")
            val employeeWithId = employee.copy(id = employeeId)

            employeesRef.child(employeeId).setValue(employeeWithId).await()
            Result.success("Employee added successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEmployee(employee: Employee): Result<String> {
        return try {
            employeesRef.child(employee.id).setValue(employee).await()
            Result.success("Employee updated successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEmployee(employeeId: String): Result<String> {
        return try {
            employeesRef.child(employeeId).removeValue().await()
            Result.success("Employee deleted successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getEmployee(employeeId: String, callback: (Employee?) -> Unit) {
        employeesRef.child(employeeId).get().addOnSuccessListener { snapshot ->
            callback(snapshot.getValue(Employee::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }
}