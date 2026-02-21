package com.lute.parse

import com.lute.domain.Language

class ParserFactory {
  private val parsers: Map<String, Parser> =
      mapOf(
          "spacedel" to SpaceDelimitedParser(),
          "turkish" to TurkishParser(),
      )

  fun getParser(parserType: String): Parser {
    return parsers[parserType] ?: throw IllegalArgumentException("Unknown parser type: $parserType")
  }

  fun getParserForLanguage(language: Language): Parser {
    return getParser(language.parserType)
  }

  fun supportedParserTypes(): Set<String> = parsers.keys
}
