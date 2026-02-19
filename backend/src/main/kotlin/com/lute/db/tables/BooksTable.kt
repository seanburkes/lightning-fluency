package com.lute.db.tables

import org.jetbrains.exposed.sql.Table

object BooksTable : Table("books") {
  val BkID = integer("BkID").autoIncrement()
  val BkLgID = integer("BkLgID").references(LanguagesTable.LgID)
  val BkTitle = varchar("BkTitle", 200)
  val BkSourceURI = varchar("BkSourceURI", 1000).nullable()
  val BkArchived = integer("BkArchived").default(0)
  val BkCurrentTxID = integer("BkCurrentTxID").default(0)
  val BkAudioFilename = text("BkAudioFilename").nullable()
  val BkAudioCurrentPos = float("BkAudioCurrentPos").nullable()
  val BkAudioBookmarks = text("BkAudioBookmarks").nullable()

  init {
    index(customIndexName = "idx_books_lgid", columns = arrayOf(BkLgID))
    index(customIndexName = "idx_books_lgid_archived", columns = arrayOf(BkLgID, BkArchived))
  }

  override val primaryKey = PrimaryKey(BkID)
}
