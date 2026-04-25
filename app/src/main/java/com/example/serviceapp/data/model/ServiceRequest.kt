package com.example.serviceapp.data.model

data class ServiceRequest(
    val id:              String = "",
    val clientId:        String = "",
    val clientName:      String = "",
    val clientPhone:     String = "",
    val serviceType:     String = "",
    val description:     String = "",
    val address:         String = "",
    // pending | awaiting_approval | accepted | completed | cancelled | no_provider
    var status:          String = "pending",
    val minRating:       Double = 0.0,
    val maxPrice:        Double = 0.0,
    val providerId:      String = "",
    val providerName:    String = "",
    val providerPhone:   String = "",
    val providerRating:  Double = 0.0,
    val providerBaseFee: Double = 0.0,
    var rating:          Int    = 0,
    var reviewComment:   String = ""
)
