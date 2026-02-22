package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BulkOperationDto(
    val operation: String,
    val term_ids: List<Long>,
    val status: Int? = null,
    val tag_ids: List<Long>? = null,
)

@Serializable
data class BulkOperationResult(
    val updated: Int,
    val failed: Int = 0,
    val errors: List<String> = emptyList(),
)

@Serializable
data class ImportResult(
    val imported: Int,
    val skipped: Int = 0,
    val errors: List<String> = emptyList(),
)
