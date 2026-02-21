package com.lute.parse

class TurkishParser : SpaceDelimitedParser() {
  override val name: String = "turkish"

  override fun getLowercase(text: String): String {
    var result = text
    result = result.replace("İ", "i")
    result = result.replace("I", "\u0131")
    return result.lowercase()
  }
}
