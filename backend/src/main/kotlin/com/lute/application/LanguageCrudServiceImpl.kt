package com.lute.application

import com.lute.application.exceptions.DuplicateLanguageException
import com.lute.application.exceptions.LanguageInUseException
import com.lute.application.exceptions.LanguageNotFoundException
import com.lute.application.exceptions.ValidationException
import com.lute.db.repositories.LanguageRepository
import com.lute.domain.Language
import com.lute.dtos.CreateLanguageDto
import com.lute.dtos.LanguageDto
import com.lute.dtos.UpdateLanguageDto
import com.lute.parse.ParserFactory

class LanguageCrudServiceImpl(
    private val languageRepository: LanguageRepository,
    private val parserFactory: ParserFactory,
    private val validationService: LanguageValidationService,
) : LanguageCrudService {
  override fun getAllLanguages(): List<LanguageDto> {
    return languageRepository.findAll().map { it.toDto() }
  }

  override fun getLanguageById(id: Long): LanguageDto? {
    return languageRepository.findById(id)?.toDto()
  }

  override fun createLanguage(dto: CreateLanguageDto): LanguageDto {
    validationService.validateLanguageNameRequired(dto.name)

    if (validationService.isDuplicateLanguageName(dto.name, excludeId = null)) {
      throw DuplicateLanguageException("Language with name '${dto.name}' already exists")
    }

    if (!validationService.validateParserType(dto.parser_type)) {
      throw ValidationException(
          listOf("parser_type" to "Invalid parser type: ${dto.parser_type}"),
      )
    }

    dto.regexp_split_sentences?.let {
      if (!validationService.validateRegex(it)) {
        throw ValidationException(
            listOf("regexp_split_sentences" to "Invalid regular expression"),
        )
      }
    }

    dto.regexp_word_characters?.let {
      if (!validationService.validateRegex(it)) {
        throw ValidationException(
            listOf("regexp_word_characters" to "Invalid regular expression"),
        )
      }
    }

    val language =
        Language(
            name = dto.name,
            parserType = dto.parser_type,
            characterSubstitutions = dto.character_substitutions,
            regexpSplitSentences = dto.regexp_split_sentences,
            exceptionsSplitSentences = dto.exceptions_split_sentences,
            regexpWordCharacters = dto.regexp_word_characters,
            rightToLeft = dto.right_to_left,
            showRomanization = dto.show_romanization,
        )

    val id = languageRepository.save(language)
    return language.copy(id = id).toDto()
  }

  override fun updateLanguage(id: Long, dto: UpdateLanguageDto): LanguageDto? {
    val existing = languageRepository.findById(id) ?: return null

    dto.name?.let { name ->
      validationService.validateLanguageNameRequired(name)

      if (validationService.isDuplicateLanguageName(name, excludeId = id)) {
        throw DuplicateLanguageException("Language with name '$name' already exists")
      }
    }

    dto.parser_type?.let {
      if (!validationService.validateParserType(it)) {
        throw ValidationException(listOf("parser_type" to "Invalid parser type: $it"))
      }
    }

    dto.regexp_split_sentences?.let {
      if (!validationService.validateRegex(it)) {
        throw ValidationException(
            listOf("regexp_split_sentences" to "Invalid regular expression"),
        )
      }
    }

    dto.regexp_word_characters?.let {
      if (!validationService.validateRegex(it)) {
        throw ValidationException(
            listOf("regexp_word_characters" to "Invalid regular expression"),
        )
      }
    }

    val updated =
        existing.copy(
            name = dto.name ?: existing.name,
            parserType = dto.parser_type ?: existing.parserType,
            characterSubstitutions =
                dto.character_substitutions?.ifEmpty { null } ?: existing.characterSubstitutions,
            regexpSplitSentences =
                dto.regexp_split_sentences?.ifEmpty { null } ?: existing.regexpSplitSentences,
            exceptionsSplitSentences =
                dto.exceptions_split_sentences?.ifEmpty { null }
                    ?: existing.exceptionsSplitSentences,
            regexpWordCharacters =
                dto.regexp_word_characters?.ifEmpty { null } ?: existing.regexpWordCharacters,
            rightToLeft = dto.right_to_left ?: existing.rightToLeft,
            showRomanization = dto.show_romanization ?: existing.showRomanization,
        )

    languageRepository.update(updated)
    return updated.toDto()
  }

  override fun deleteLanguage(id: Long) {
    val language =
        languageRepository.findById(id)
            ?: throw LanguageNotFoundException(
                "Language with id $id not found",
            )

    val bookCount = languageRepository.countBooksForLanguage(id)
    if (bookCount > 0) {
      throw LanguageInUseException(
          "Cannot delete language '${language.name}' because it has $bookCount book(s)",
      )
    }

    val termCount = languageRepository.countTermsForLanguage(id)
    if (termCount > 0) {
      throw LanguageInUseException(
          "Cannot delete language '${language.name}' because it has $termCount term(s)",
      )
    }

    val dictCount = languageRepository.countDictionariesForLanguage(id)
    if (dictCount > 0) {
      throw LanguageInUseException(
          "Cannot delete language '${language.name}' because it has $dictCount dictionary/dictionaries",
      )
    }

    languageRepository.delete(id)
  }

  private fun Language.toDto(): LanguageDto {
    return LanguageDto(
        id = id,
        name = name,
        parser_type = parserType,
        character_substitutions = characterSubstitutions,
        regexp_split_sentences = regexpSplitSentences,
        exceptions_split_sentences = exceptionsSplitSentences,
        regexp_word_characters = regexpWordCharacters,
        right_to_left = rightToLeft,
        show_romanization = showRomanization,
    )
  }
}
