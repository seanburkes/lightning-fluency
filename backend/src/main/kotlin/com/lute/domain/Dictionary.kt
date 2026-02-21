package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Dictionary(
    val id: Long = 0,
    val languageId: Long,
    val useFor: String,
    val type: String,
    val dictUri: String,
    val isActive: Boolean = true,
    val sortOrder: Int,
)
