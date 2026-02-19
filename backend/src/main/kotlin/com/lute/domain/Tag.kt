package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Int = 0,
    val text: String,
    val comment: String = "",
)
