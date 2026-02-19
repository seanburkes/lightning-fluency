package com.lute.domain

import java.time.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Text(
    val id: Int = 0,
    val bookId: Int,
    val order: Int,
    val text: String,
    @Contextual val readDate: LocalDateTime? = null,
    val wordCount: Int? = null,
    @Contextual val startDate: LocalDateTime? = null,
)
