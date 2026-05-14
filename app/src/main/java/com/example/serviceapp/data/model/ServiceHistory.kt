package com.example.serviceapp.data.model

data class ServiceHistory(
    val id:                    String = "",
    val description:           String = "",
    val earning:               Double = 0.0,
    val clientName:            String = "",
    val address:               String = "",  // job location
    val completedAt:           String = "",  // formatted completion date
    val clientRating:          Int    = 0,   // rating the client gave to provider
    var providerRatingForClient: Int  = 0    // rating the provider gives to client
)
