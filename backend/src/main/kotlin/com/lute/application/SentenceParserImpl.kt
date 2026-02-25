package com.lute.application

import com.lute.domain.Language

class SentenceParserImpl(private val parserService: ParserService) : SentenceParser {
  override fun parseSentences(text: String, language: Language): ParsedSentences {
    val tokens = parserService.parseText(text, language)
    return ParsedSentences(tokens = tokens)
  }
}
