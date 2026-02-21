package com.lute.application

import com.lute.domain.Language
import com.lute.parse.ParsedToken
import com.lute.parse.ParserFactory

class ParserServiceImpl(private val parserFactory: ParserFactory) : ParserService {
  override fun parseText(text: String, language: Language): List<ParsedToken> {
    val parser = parserFactory.getParserForLanguage(language)
    return parser.parse(text, language)
  }

  override fun getReading(text: String, language: Language): String? {
    val parser = parserFactory.getParserForLanguage(language)
    return parser.getReading(text)
  }

  override fun getLowercase(text: String, language: Language): String {
    val parser = parserFactory.getParserForLanguage(language)
    return parser.getLowercase(text)
  }
}
