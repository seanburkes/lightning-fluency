package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TermDto(
    val id: Long,
    val text: String,
    val language_id: Long,
    val status: Int = 0,
    val translation: String? = null,
    val romanization: String? = null,
    val token_count: Int = 1,
    val tags: List<String> = emptyList(),
    val parents: List<String> = emptyList(),
    val children_count: Int = 0,
    val created_at: String? = null,
    val status_changed_at: String? = null,
)

@Serializable
data class CreateTermDto(
    val text: String,
    val language_id: Long,
    val translation: String? = null,
    val romanization: String? = null,
    val status: Int = 0,
    val tags: List<Long> = emptyList(),
)

@Serializable
data class UpdateTermDto(
    val text: String? = null,
    val translation: String? = null,
    val romanization: String? = null,
    val status: Int? = null,
)
