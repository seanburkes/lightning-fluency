package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val id: Int = 0,
    val text: String,
    val abbreviation: String,
)
