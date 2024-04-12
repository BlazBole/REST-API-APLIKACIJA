package com.example.myapiapp

data class ContactItem(
    val userId: Int?,
    val userName: String,
    val email: String,
    val password: String,
    val phone: String,
    val image: String
)