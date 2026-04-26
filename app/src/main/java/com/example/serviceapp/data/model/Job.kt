package com.example.serviceapp.data.model

data class Job(
    val id: String,
    val description: String,
    val address: String,
    val phone: String,
    val overview: String = "",
    var status: String = "pending",
    val problemType: String = "normal"   // "normal" | "advanced" | "critical"
)
