package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object BookStatsTable : Table("bookstats") {
  val BsBkID = long("BsBkID").references(BooksTable.BkID)
  val BsDistinctTerms = integer("BsDistinctTerms").nullable()
  val BsDistinctUnknowns = integer("BsDistinctUnknowns").nullable()
  val BsUnknownPercent = integer("BsUnknownPercent").nullable()
  val BsStatusDistribution = varchar("BsStatusDistribution", 100).nullable()

  override val primaryKey = PrimaryKey(BsBkID)
}
