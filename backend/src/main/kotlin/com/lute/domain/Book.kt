package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int = 0,
    val languageId: Int,
    val title: String,
    val sourceURI: String? = null,
    val archived: Boolean = false,
    val currentTextId: Int = 0,
    val audioFilename: String? = null,
    val audioCurrentPos: Float? = null,
    val audioBookmarks: String? = null,
)
