package com.lute.application

import com.lute.application.exceptions.EntityNotFoundException
import com.lute.db.repositories.BookRepository
import com.lute.db.repositories.DictionaryRepository
import com.lute.db.repositories.LanguageRepository
import com.lute.db.repositories.StatusRepository
import com.lute.db.repositories.TagRepository
import com.lute.db.repositories.TermRepository
import com.lute.db.repositories.TextRepository
import com.lute.domain.Book
import com.lute.domain.Dictionary
import com.lute.domain.Language
import com.lute.domain.Status
import com.lute.domain.Tag
import com.lute.domain.Term
import com.lute.domain.Text

fun LanguageRepository.require(id: Long, entityName: String = "Language"): Language {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun BookRepository.require(id: Long, entityName: String = "Book"): Book {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun TermRepository.require(id: Long, entityName: String = "Term"): Term {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun TagRepository.require(id: Long, entityName: String = "Tag"): Tag {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun StatusRepository.require(id: Long, entityName: String = "Status"): Status {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun TextRepository.require(id: Long, entityName: String = "Text"): Text {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun DictionaryRepository.require(id: Long, entityName: String = "Dictionary"): Dictionary {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}
