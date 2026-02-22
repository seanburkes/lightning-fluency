package com.lute.application.exceptions

class TermNotFoundException(message: String) : RuntimeException(message)

class DuplicateTermException(message: String) : RuntimeException(message)
