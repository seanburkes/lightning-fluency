package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object SrsExportSpecsTable : Table("srsexportspecs") {
  val SrsID = integer("SrsID").autoIncrement()
  val SrsExportName = varchar("SrsExportName", 200).uniqueIndex()
  val SrsCriteria = varchar("SrsCriteria", 1000)
  val SrsDeckName = varchar("SrsDeckName", 200)
  val SrsNoteType = varchar("SrsNoteType", 200)
  val SrsFieldMapping = varchar("SrsFieldMapping", 1000)
  val SrsActive = integer("SrsActive").default(1)

  override val primaryKey = PrimaryKey(SrsID)
}
