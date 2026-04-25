package com.example.serviceapp.data.model

data class Provider(
    val id: String,
    var name: String,
    var phone: String,
    var photo: String,
    var email: String = "",
    var nid: String = "",
    var serviceType: String = "",
    var baseFee: Double = 300.0,
    var availability: String = "available",   // "available" | "working" | "unavailable"
    var rating: Double = 4.5,
    var due: Double = 0.0,
    var advance: Double = 0.0,
    val history: MutableList<ServiceHistory> = mutableListOf(),
    var certificate: String = "",
    var isApproved: Boolean? = null   // null = pending, true = approved, false = rejected
)
