package com.example.photoeditor

import java.io.Serializable
import java.time.LocalDate

data class Photo(
    val photoId: String,
    val userId: String,
    val original: Boolean,
    val path: String,
    val createdAt: LocalDate
) : Serializable