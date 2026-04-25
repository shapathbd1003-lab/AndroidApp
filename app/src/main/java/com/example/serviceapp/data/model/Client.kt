package com.example.serviceapp.data.model

data class Client(
    val id:     String,
    var name:   String,
    var phone:  String,
    var email:  String = "",
    var avatar: String = ""   // gallery URI or "colorInt:emoji"
)
