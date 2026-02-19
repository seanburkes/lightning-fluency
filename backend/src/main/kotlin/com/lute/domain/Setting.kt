package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable
data class Setting(
    val key: String,
    val keyType: String,
    val value: String? = null,
)
