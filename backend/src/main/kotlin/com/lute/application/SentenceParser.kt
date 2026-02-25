package com.lute.application

import com.lute.domain.Language
import com.lute.parse.ParsedToken

interface SentenceParser {
  fun parseSentences(text: String, language: Language): ParsedSentences
}

data class ParsedSentences(val tokens: List<ParsedToken>) {
  fun findSentenceForWord(word: String): String? {
    val wordLower = word.lowercase()
    val matchingToken = tokens.find { it.token.lowercase() == wordLower } ?: return null
    val sentenceNumber = matchingToken.sentenceNumber
    return extractSentence(sentenceNumber)
  }

  fun extractContext(word: String, contextWindow: Int = 1): String? {
    val wordLower = word.lowercase()
    val matchingToken = tokens.find { it.token.lowercase() == wordLower } ?: return null
    val sentenceNumber = matchingToken.sentenceNumber
    val contextStart = (sentenceNumber - contextWindow).coerceAtLeast(1)
    val contextEnd = sentenceNumber + contextWindow
    return extractSentences(contextStart..contextEnd)
  }

  fun extractSentence(sentenceNumber: Int): String? {
    val sentenceTokens = tokens.filter { it.sentenceNumber == sentenceNumber }
    return if (sentenceTokens.isNotEmpty()) {
      sentenceTokens.joinToString("") { it.token }.trim()
    } else {
      null
    }
  }

  fun extractSentences(sentenceRange: IntRange): String? {
    val contextTokens = tokens.filter { it.sentenceNumber in sentenceRange }
    return if (contextTokens.isNotEmpty()) {
      contextTokens.joinToString("") { it.token }.trim()
    } else {
      null
    }
  }
}
