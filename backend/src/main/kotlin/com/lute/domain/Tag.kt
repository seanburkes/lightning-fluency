package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Long = 0,
    val text: String,
    val comment: String = "",
)
