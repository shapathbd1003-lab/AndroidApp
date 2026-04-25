package com.example.serviceapp.data.model

data class ServiceRequest(
    val id:            String = "",
    val clientId:      String = "",
    val clientName:    String = "",
    val clientPhone:   String = "",
    val serviceType:   String = "",
    val description:   String = "",
    val address:       String = "",
    var status:        String = "pending",   // pending | accepted | completed
    val providerId:    String = "",
    val providerName:  String = "",
    val providerPhone: String = "",
    var rating:        Int    = 0            // 0 = not rated yet
)
