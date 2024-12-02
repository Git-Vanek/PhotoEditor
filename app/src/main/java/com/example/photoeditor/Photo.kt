package com.example.photoeditor

import java.io.Serializable

data class Photo(
    val createdAt: String,
    val original: Boolean,
    val path: String,
    val private: Boolean
) : Serializable