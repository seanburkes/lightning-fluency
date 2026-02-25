package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class StatsDto(
    val total_terms: Int = 0,
    val status_distribution: Map<String, Int> = emptyMap(),
)
