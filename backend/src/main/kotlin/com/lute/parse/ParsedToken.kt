package com.lute.parse

data class ParsedToken(
    val token: String,
    val isWord: Boolean,
    val isEndOfSentence: Boolean = false,
    val order: Int = 0,
    val sentenceNumber: Int = 0,
)
