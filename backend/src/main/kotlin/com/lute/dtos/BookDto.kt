package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val id: Long,
    val title: String,
    val language_id: Long,
    val language_name: String,
    val source_uri: String? = null,
    val archived: Boolean = false,
    val page_count: Int = 0,
    val current_page: Int = 0,
    val tags: List<String> = emptyList(),
    val created_at: String? = null,
)

@Serializable
data class CreateBookDto(
    val title: String,
    val language_id: Long,
    val content: String? = null,
    val source_uri: String? = null,
)

@Serializable
data class UpdateBookDto(
    val title: String? = null,
    val archived: Boolean? = null,
)

@Serializable data class AddTagToBookDto(val tag_id: Long)
