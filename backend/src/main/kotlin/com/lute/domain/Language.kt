package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val id: Long = 0,
    val name: String,
    val characterSubstitutions: String? = null,
    val regexpSplitSentences: String? = null,
    val exceptionsSplitSentences: String? = null,
    val regexpWordCharacters: String? = null,
    val rightToLeft: Boolean = false,
    val showRomanization: Boolean = false,
    val parserType: String = "spacedel",
)
