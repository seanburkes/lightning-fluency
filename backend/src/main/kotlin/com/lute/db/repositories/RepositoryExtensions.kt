package com.lute.db.repositories

import com.lute.application.exceptions.EntityNotFoundException
import com.lute.domain.Book
import com.lute.domain.Dictionary
import com.lute.domain.Language
import com.lute.domain.Status
import com.lute.domain.Tag
import com.lute.domain.Term
import com.lute.domain.Text

fun LanguageRepository.require(id: Long, entityName: String): Language {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun BookRepository.require(id: Long, entityName: String): Book {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun TermRepository.require(id: Long, entityName: String): Term {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun TagRepository.require(id: Long, entityName: String): Tag {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun StatusRepository.require(id: Long, entityName: String): Status {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun TextRepository.require(id: Long, entityName: String): Text {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}

fun DictionaryRepository.require(id: Long, entityName: String): Dictionary {
  return findById(id) ?: throw EntityNotFoundException(entityName, id)
}
