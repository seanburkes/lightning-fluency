package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PopupDto(
    val term: TermDto? = null,
    val sentence: String? = null,
    val context: String? = null,
)
