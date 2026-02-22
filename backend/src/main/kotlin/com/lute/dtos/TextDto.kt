package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TextDto(
    val id: Long,
    val book_id: Long,
    val order: Int,
    val text: String,
    val read_date: String? = null,
    val word_count: Int? = null,
)

@Serializable
data class CreateTextDto(
    val book_id: Long,
    val order: Int,
    val text: String,
)
