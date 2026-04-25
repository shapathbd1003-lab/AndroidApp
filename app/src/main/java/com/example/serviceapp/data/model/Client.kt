package com.example.serviceapp.data.model

data class Client(
    val id:    String,
    var name:  String,
    var phone: String,
    var email: String = ""
)
