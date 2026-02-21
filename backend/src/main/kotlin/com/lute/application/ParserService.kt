package com.lute.application

import com.lute.domain.Language
import com.lute.parse.ParsedToken

interface ParserService {
  fun parseText(text: String, language: Language): List<ParsedToken>

  fun getReading(text: String, language: Language): String?

  fun getLowercase(text: String, language: Language): String
}
