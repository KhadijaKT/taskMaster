package com.example.taskmaster

data class DeleteRequest(
    val taskId: String = "",
    val taskName: String = "",
    val reason: String = "",
    val requestedById: String = "",
    val requestedByName: String = "",
    val requestedAt: Long = System.currentTimeMillis()
)