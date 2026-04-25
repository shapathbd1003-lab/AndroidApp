package com.example.serviceapp.utils

fun Double.toBDT(): String = "৳ ${"%.0f".format(this)}"
