package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BookStatsDto(
    val book_id: Long,
    val distinct_terms: Int = 0,
    val distinct_unknowns: Int = 0,
    val unknown_percent: Int = 0,
    val status_distribution: Map<String, Int> = emptyMap(),
)

@Serializable
data class TagDto(
    val id: Long,
    val text: String,
    val comment: String = "",
)
