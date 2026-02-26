package com.lute.parse

import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import com.lute.domain.Language

class JapaneseParser : Parser {
  private val tokenizer = Tokenizer.Builder().build()

  override val name: String = "Japanese"

  fun supportedLanguageCodes(): Set<String> = setOf("ja")

  override fun parse(text: String, language: Language): List<ParsedToken> {
    val tokens = tokenizer.tokenize(text)
    val result = mutableListOf<ParsedToken>()
    var order = 0
    var sentenceNumber = 0

    for (token in tokens) {
      val isWord = isContentWord(token)
      val isEndOfSentence = token.surface == "。" || token.surface == "！" || token.surface == "？"

      result.add(
          ParsedToken(
              token = token.surface,
              isWord = isWord,
              isEndOfSentence = isEndOfSentence,
              order = order++,
              sentenceNumber = sentenceNumber,
          ),
      )
      if (isEndOfSentence) {
        sentenceNumber++
      }
    }

    return result
  }

  override fun getReading(text: String, format: String): String? {
    if (isAllHiragana(text)) {
      return null
    }

    when (format) {
      "furigana" -> return getFurigana(text)
      "html-furigana" -> return getHtmlFurigana(text)
    }

    val tokens = tokenizer.tokenize(text)

    val readings = mutableListOf<String>()
    for (token in tokens) {
      val reading = token.reading
      if (hasValidReading(reading)) {
        val normalizedReading = normalizeReading(reading!!, format)
        if (normalizedReading.isNotBlank()) {
          readings.add(normalizedReading)
        }
      }
    }

    if (readings.isEmpty()) {
      return null
    }

    val result = readings.joinToString("")
    return if (result == text) null else result
  }

  override fun getLowercase(text: String): String {
    val tokens = tokenizer.tokenize(text)
    val result = StringBuilder()

    for (token in tokens) {
      val reading = token.reading
      if (reading != null && reading.isNotBlank()) {
        result.append(toHiragana(reading))
      } else {
        result.append(token.surface)
      }
    }

    return result.toString()
  }

  private fun getFurigana(text: String): String {
    if (text.isEmpty()) {
      return ""
    }

    val tokens = tokenizer.tokenize(text)
    val result = StringBuilder()

    for (token in tokens) {
      val surface = token.surface
      val reading = token.reading
      val hasKanjiReading = hasValidReading(reading) && containsKanji(surface)

      if (hasKanjiReading) {
        val hiraganaReading = toHiragana(reading!!)
        result.append(surface)
        result.append("(")
        result.append(hiraganaReading)
        result.append(")")
      } else {
        result.append(surface)
      }
    }

    return result.toString()
  }

  private fun getHtmlFurigana(text: String): String {
    if (text.isEmpty()) {
      return ""
    }

    val tokens = tokenizer.tokenize(text)
    val result = StringBuilder()

    for (token in tokens) {
      val surface = token.surface
      val reading = token.reading
      val hasKanjiReading = hasValidReading(reading) && containsKanji(surface)

      if (hasKanjiReading) {
        val hiraganaReading = toHiragana(reading!!)
        result.append("<ruby>")
        result.append(surface)
        result.append("<rt>")
        result.append(hiraganaReading)
        result.append("</rt></ruby>")
      } else {
        result.append(surface)
      }
    }

    return result.toString()
  }

  private fun isContentWord(token: Token): Boolean {
    val pos = token.partOfSpeechLevel1
    return pos.startsWith("名詞") ||
        pos.startsWith("動詞") ||
        pos.startsWith("形容詞") ||
        pos.startsWith("形容動詞") ||
        pos.startsWith("副詞")
  }

  private fun isAllHiragana(text: String): Boolean {
    return text.all { c -> !isJapanese(c) || isHiragana(c) }
  }

  private fun isJapanese(c: Char): Boolean {
    val code = c.code
    return (code in 0x3040..0x309F) || (code in 0x30A0..0x30FF) || (code in 0x4E00..0x9FFF)
  }

  private fun isHiragana(c: Char): Boolean {
    return c.code in 0x3040..0x309F
  }

  private fun containsKanji(text: String): Boolean {
    return text.any { c -> c.code in 0x4E00..0x9FFF }
  }

  private fun hasValidReading(reading: String?): Boolean {
    return reading != null && reading.isNotBlank() && !reading.contains("�")
  }

  private fun normalizeReading(reading: String, format: String): String {
    return when (format) {
      "hiragana" -> toHiragana(reading)
      "katakana" -> reading
      "alphabet" -> katakanaToRomaji(reading)
      "romaji" -> katakanaToRomaji(reading)
      else -> reading
    }
  }

  private fun katakanaToRomaji(katakana: String): String {
    val result = StringBuilder()
    var i = 0
    while (i < katakana.length) {
      val c = katakana[i]

      // Small tsu (sokuon) — doubles the next consonant
      if (c == 'ッ') {
        if (i + 1 < katakana.length) {
          val nextTwoChar = if (i + 3 <= katakana.length) katakana.substring(i + 1, i + 3) else ""
          val nextOneChar = katakana.substring(i + 1, i + 2)
          val nextRomaji = KATAKANA_TO_ROMAJI[nextTwoChar] ?: KATAKANA_TO_ROMAJI[nextOneChar] ?: ""
          if (nextRomaji.isNotEmpty()) {
            result.append(nextRomaji[0])
          }
        }
        i += 1
        continue
      }

      // Long vowel mark — repeats the previous vowel
      if (c == 'ー') {
        if (result.isNotEmpty()) {
          result.append(result.last())
        }
        i += 1
        continue
      }

      val twoChar = if (i + 2 <= katakana.length) katakana.substring(i, i + 2) else ""
      val oneChar = katakana.substring(i, i + 1)

      when {
        twoChar in KATAKANA_TO_ROMAJI -> {
          result.append(KATAKANA_TO_ROMAJI[twoChar])
          i += 2
        }
        oneChar in KATAKANA_TO_ROMAJI -> {
          result.append(KATAKANA_TO_ROMAJI[oneChar])
          i += 1
        }
        else -> {
          result.append(oneChar)
          i += 1
        }
      }
    }
    return result.toString()
  }

  private fun toHiragana(katakana: String): String {
    val result = StringBuilder()
    for (c in katakana) {
      val code = c.code
      if (code in 0x30A0..0x30FF) {
        result.append((code - 0x60).toChar())
      } else {
        result.append(c)
      }
    }
    return result.toString()
  }

  companion object {
    private val KATAKANA_TO_ROMAJI =
        mapOf(
            "ア" to "a",
            "イ" to "i",
            "ウ" to "u",
            "エ" to "e",
            "オ" to "o",
            "カ" to "ka",
            "キ" to "ki",
            "ク" to "ku",
            "ケ" to "ke",
            "コ" to "ko",
            "サ" to "sa",
            "シ" to "shi",
            "ス" to "su",
            "セ" to "se",
            "ソ" to "so",
            "タ" to "ta",
            "チ" to "chi",
            "ツ" to "tsu",
            "テ" to "te",
            "ト" to "to",
            "ナ" to "na",
            "ニ" to "ni",
            "ヌ" to "nu",
            "ネ" to "ne",
            "ノ" to "no",
            "ハ" to "ha",
            "ヒ" to "hi",
            "フ" to "fu",
            "ヘ" to "he",
            "ホ" to "ho",
            "マ" to "ma",
            "ミ" to "mi",
            "ム" to "mu",
            "メ" to "me",
            "モ" to "mo",
            "ヤ" to "ya",
            "ユ" to "yu",
            "ヨ" to "yo",
            "ラ" to "ra",
            "リ" to "ri",
            "ル" to "ru",
            "レ" to "re",
            "ロ" to "ro",
            "ワ" to "wa",
            "ヲ" to "wo",
            "ン" to "n",
            "ガ" to "ga",
            "ギ" to "gi",
            "グ" to "gu",
            "ゲ" to "ge",
            "ゴ" to "go",
            "ザ" to "za",
            "ジ" to "ji",
            "ズ" to "zu",
            "ゼ" to "ze",
            "ゾ" to "zo",
            "ダ" to "da",
            "ヂ" to "ji",
            "ヅ" to "zu",
            "デ" to "de",
            "ド" to "do",
            "バ" to "ba",
            "ビ" to "bi",
            "ブ" to "bu",
            "ベ" to "be",
            "ボ" to "bo",
            "パ" to "pa",
            "ピ" to "pi",
            "プ" to "pu",
            "ペ" to "pe",
            "ポ" to "po",
            "キャ" to "kya",
            "キュ" to "kyu",
            "キョ" to "kyo",
            "シャ" to "sha",
            "シュ" to "shu",
            "ショ" to "sho",
            "チャ" to "cha",
            "チュ" to "chu",
            "チョ" to "cho",
            "ニャ" to "nya",
            "ニュ" to "nyu",
            "ニョ" to "nyo",
            "ヒャ" to "hya",
            "ヒュ" to "hyu",
            "ヒョ" to "hyo",
            "ミャ" to "mya",
            "ミュ" to "myu",
            "ミョ" to "myo",
            "リャ" to "rya",
            "リュ" to "ryu",
            "リョ" to "ryo",
            "ジェ" to "je",
        )
  }
}
