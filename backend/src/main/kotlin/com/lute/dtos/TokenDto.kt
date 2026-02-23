package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    val token: String,
    val is_word: Boolean,
    val status: Int? = null,
    val term_id: Long? = null,
    val translation: String? = null,
    val romanization: String? = null,
)
