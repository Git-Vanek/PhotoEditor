package com.example.photoeditor

import java.io.Serializable

data class Photo(
    val id: String,
    val createdAt: String,
    val original: Boolean,
    val path: String,
    val private: Boolean
) : Serializable