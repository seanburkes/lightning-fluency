package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class BookStats(
    val bookId: Long,
    val distinctTerms: Int? = null,
    val distinctUnknowns: Int? = null,
    val unknownPercent: Int? = null,
    val statusDistribution: String? = null,
)
