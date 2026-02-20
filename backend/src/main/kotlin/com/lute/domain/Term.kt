package com.lute.domain

import java.time.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Term(
    val id: Long = 0,
    val languageId: Long,
    val text: String,
    val textLC: String = "",
    val status: Int = 0,
    val translation: String? = null,
    val romanization: String? = null,
    val tokenCount: Int = 1,
    @Contextual val created: LocalDateTime? = null,
    @Contextual val statusChanged: LocalDateTime? = null,
    val syncStatus: Int = 0,
)
