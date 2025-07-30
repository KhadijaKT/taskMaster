package com.example.taskmaster

data class Task(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val assignedTo: String = "", // Employee ID
    val assignedToName: String = "", // Employee name for display
    val assignedById: String = "",  // Admin UID
    val assignedByName: String = "", // Admin name
    val status: String = "Pending", // Pending, In Progress, Completed
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null
)

