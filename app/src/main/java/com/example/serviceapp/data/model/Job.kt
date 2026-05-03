package com.example.serviceapp.data.model

data class Job(
    val id:          String,
    val description: String,
    val address:     String,
    val phone:       String,
    val overview:    String = "",
    var status:      String = "pending",
    val problemType: String = "normal",   // "normal" | "advanced" | "critical"
    val lat:         Double = 0.0,
    val lng:         Double = 0.0,
    var distanceKm:  Double = -1.0        // -1 = unknown
)
