package com.lute.application.exceptions

class BookNotFoundException(message: String) : RuntimeException(message)

class TagNotFoundException(message: String) : RuntimeException(message)
