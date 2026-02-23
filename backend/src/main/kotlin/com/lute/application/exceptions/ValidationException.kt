package com.lute.application.exceptions

class ValidationException(val errors: List<Pair<String, String>>) :
    RuntimeException("Validation failed")
