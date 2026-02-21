package com.lute.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ValidationError(
    val field: String,
    val message: String,
)

@Serializable
data class ValidationErrorResponse(
    val errors: List<ValidationError>,
)
