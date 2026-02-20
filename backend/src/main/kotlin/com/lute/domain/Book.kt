package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Long = 0,
    val languageId: Long,
    val title: String,
    val sourceURI: String? = null,
    val archived: Boolean = false,
    val currentTextId: Long = 0,
    val audioFilename: String? = null,
    val audioCurrentPos: Double? = null,
    val audioBookmarks: String? = null,
)
