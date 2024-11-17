package com.example.photoeditor

import java.time.LocalDate

data class User (
    val userId: String,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: LocalDate
)