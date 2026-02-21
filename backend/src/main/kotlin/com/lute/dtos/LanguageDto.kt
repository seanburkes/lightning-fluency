package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LanguageDto(
    val id: Long,
    val name: String,
    val parser_type: String,
    val character_substitutions: String? = null,
    val regexp_split_sentences: String? = null,
    val exceptions_split_sentences: String? = null,
    val regexp_word_characters: String? = null,
    val right_to_left: Boolean = false,
    val show_romanization: Boolean = false,
)

@Serializable
data class CreateLanguageDto(
    val name: String,
    val parser_type: String,
    val character_substitutions: String? = null,
    val regexp_split_sentences: String? = null,
    val exceptions_split_sentences: String? = null,
    val regexp_word_characters: String? = null,
    val right_to_left: Boolean = false,
    val show_romanization: Boolean = false,
)

@Serializable
data class UpdateLanguageDto(
    val name: String? = null,
    val parser_type: String? = null,
    val character_substitutions: String? = null,
    val regexp_split_sentences: String? = null,
    val exceptions_split_sentences: String? = null,
    val regexp_word_characters: String? = null,
    val right_to_left: Boolean? = null,
    val show_romanization: Boolean? = null,
)
