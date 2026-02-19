package com.lute.domain

import kotlinx.serialization.Serializable

@Serializable data class HealthResponse(val status: String)

@Serializable data class ErrorResponse(val error: String)
