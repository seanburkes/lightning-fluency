package com.lute.parse

import com.lute.domain.Language

interface Parser {
  val name: String

  fun parse(text: String, language: Language): List<ParsedToken>

  fun getReading(text: String): String?

  fun getLowercase(text: String): String
}
