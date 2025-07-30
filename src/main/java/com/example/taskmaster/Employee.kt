// Employee.kt
package com.example.taskmaster

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Employee(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", 0L)
}