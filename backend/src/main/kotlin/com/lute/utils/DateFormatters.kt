package com.lute.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateFormatters {
  val ISO_LOCAL: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  fun LocalDateTime?.toIsoString(): String? = this?.format(ISO_LOCAL)
}
