package com.example.photoeditor

import java.time.LocalDate

data class Photo(
    val photoId: String,
    val userId: String,
    val name: String,
    val original: Boolean,
    val path: String,
    val createdAt: LocalDate
)