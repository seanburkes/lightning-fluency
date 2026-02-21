package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class DictionaryDto(
    val id: Long,
    val language_id: Long,
    val ld_use_for: String,
    val ld_type: String,
    val ld_dict_uri: String,
    val ld_is_active: Boolean,
    val ld_sort_order: Int,
)

@Serializable
data class CreateDictionaryDto(
    val ld_use_for: String,
    val ld_type: String,
    val ld_dict_uri: String,
    val ld_sort_order: Int,
    val ld_is_active: Boolean = true,
)

@Serializable
data class UpdateDictionaryDto(
    val ld_use_for: String? = null,
    val ld_type: String? = null,
    val ld_dict_uri: String? = null,
    val ld_sort_order: Int? = null,
    val ld_is_active: Boolean? = null,
)
