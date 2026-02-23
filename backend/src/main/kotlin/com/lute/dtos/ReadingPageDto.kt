package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ReadingPageDto(
    val page: PageDto,
    val tokens: List<TokenDto>,
)

@Serializable
data class PageDto(
    val id: Long,
    val order: Int,
    val text: String,
)
