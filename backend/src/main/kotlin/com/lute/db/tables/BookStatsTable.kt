package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object BookStatsTable : Table("bookstats") {
  val BkID = integer("BkID").references(BooksTable.BkID)
  val distinctterms = integer("distinctterms").nullable()
  val distinctunknowns = integer("distinctunknowns").nullable()
  val unknownpercent = integer("unknownpercent").nullable()
  val status_distribution = varchar("status_distribution", 100).nullable()

  override val primaryKey = PrimaryKey(BkID)
}
