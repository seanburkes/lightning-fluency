package com.lute.parse

import com.lute.domain.Language

open class SpaceDelimitedParser : Parser {
  override val name: String = "spacedel"

  override fun parse(text: String, language: Language): List<ParsedToken> {
    val cleaned =
        CharacterSubstitution.apply(text, language.characterSubstitutions)
            .replace(MULTI_SPACE, " ")
            .replace("\u200B", "")
            .replace("\r\n", "\n")
            .replace("{", "[")
            .replace("}", "]")

    val tokens = mutableListOf<ParsedToken>()
    var order = 0
    var sentenceNumber = 0

    val paragraphs = cleaned.split("\n")
    for ((i, paragraph) in paragraphs.withIndex()) {
      val result = parseParagraph(paragraph, language, order, sentenceNumber)
      tokens.addAll(result.tokens)
      order = result.nextOrder
      sentenceNumber = result.nextSentenceNumber

      if (i != paragraphs.size - 1) {
        tokens.add(
            ParsedToken(
                token = "\u00B6",
                isWord = false,
                isEndOfSentence = true,
                order = order++,
                sentenceNumber = sentenceNumber++,
            ),
        )
      }
    }

    return tokens
  }

  override fun getReading(text: String): String? = null

  override fun getLowercase(text: String): String = text.lowercase()

  private fun parseParagraph(
      text: String,
      language: Language,
      startOrder: Int,
      startSentenceNumber: Int,
  ): ParagraphResult {
    val wordChars = language.regexpWordCharacters?.takeIf { it.isNotBlank() } ?: ""
    val pattern = SentenceSplitter.buildTokenPattern(wordChars, language.exceptionsSplitSentences)

    val tokens = mutableListOf<ParsedToken>()
    var order = startOrder
    var sentenceNumber = startSentenceNumber
    var pos = 0

    for (match in pattern.findAll(text)) {
      val word = match.value
      val wordStart = match.range.first

      if (wordStart > pos) {
        val nonWord = text.substring(pos, wordStart)
        val isEos = SentenceSplitter.containsSentenceEnd(nonWord, language.regexpSplitSentences)
        tokens.add(
            ParsedToken(
                token = nonWord,
                isWord = false,
                isEndOfSentence = isEos,
                order = order++,
                sentenceNumber = sentenceNumber,
            ),
        )
        if (isEos) sentenceNumber++
      }

      tokens.add(
          ParsedToken(
              token = word,
              isWord = true,
              isEndOfSentence = false,
              order = order++,
              sentenceNumber = sentenceNumber,
          ),
      )
      pos = match.range.last + 1
    }

    if (pos < text.length) {
      val trailing = text.substring(pos)
      val isEos = SentenceSplitter.containsSentenceEnd(trailing, language.regexpSplitSentences)
      tokens.add(
          ParsedToken(
              token = trailing,
              isWord = false,
              isEndOfSentence = isEos,
              order = order++,
              sentenceNumber = sentenceNumber,
          ),
      )
      if (isEos) sentenceNumber++
    }

    return ParagraphResult(tokens, order, sentenceNumber)
  }

  private data class ParagraphResult(
      val tokens: List<ParsedToken>,
      val nextOrder: Int,
      val nextSentenceNumber: Int,
  )

  companion object {
    private val MULTI_SPACE = Regex(" +")
  }
}
