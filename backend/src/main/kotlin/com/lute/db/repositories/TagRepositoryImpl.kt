package com.lute.db.repositories

import com.lute.db.Mappers.toBookTag
import com.lute.db.Mappers.toTag
import com.lute.db.tables.BookTagsTable
import com.lute.db.tables.Tags2Table
import com.lute.db.tables.TagsTable
import com.lute.db.tables.WordTagsTable
import com.lute.domain.Tag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction

class TagRepositoryImpl : TagRepository {
  override fun findAll(): List<Tag> = transaction { TagsTable.selectAll().map { it.toTag() } }

  override fun findById(id: Long): Tag? = transaction {
    TagsTable.selectAll().where { TagsTable.TgID eq id }.singleOrNull()?.toTag()
  }

  override fun findByText(text: String): Tag? = transaction {
    TagsTable.selectAll().where { TagsTable.TgText eq text }.singleOrNull()?.toTag()
  }

  override fun save(tag: Tag): Long = transaction {
    TagsTable.insert {
          it[TgText] = tag.text
          it[TgComment] = tag.comment
        }[TagsTable.TgID]
  }

  override fun addTagToTerm(termId: Long, tagId: Long): Unit = transaction {
    val existing =
        WordTagsTable.selectAll()
            .where { (WordTagsTable.WtWoID eq termId) and (WordTagsTable.WtTgID eq tagId) }
            .singleOrNull()
    if (existing == null) {
      WordTagsTable.insert {
        it[WtWoID] = termId
        it[WtTgID] = tagId
      }
    }
  }

  override fun addTagsToTerms(termIds: List<Long>, tagIds: List<Long>): Int = transaction {
    if (termIds.isEmpty() || tagIds.isEmpty()) {
      return@transaction 0
    }
    val existingPairs =
        WordTagsTable.selectAll()
            .where {
              (WordTagsTable.WtWoID inList termIds) and (WordTagsTable.WtTgID inList tagIds)
            }
            .map { Pair(it[WordTagsTable.WtWoID], it[WordTagsTable.WtTgID]) }
            .toSet()
    val newPairs = termIds.flatMap { termId -> tagIds.map { tagId -> Pair(termId, tagId) } }
    val toInsert = newPairs.filter { it !in existingPairs }
    for ((termId, tagId) in toInsert) {
      WordTagsTable.insert {
        it[WtWoID] = termId
        it[WtTgID] = tagId
      }
    }
    toInsert.size
  }

  override fun removeTagFromTerm(termId: Long, tagId: Long): Unit = transaction {
    WordTagsTable.deleteWhere { (WtWoID eq termId) and (WtTgID eq tagId) }
  }

  override fun removeTagsFromTerms(termIds: List<Long>, tagIds: List<Long>): Int = transaction {
    if (termIds.isEmpty() || tagIds.isEmpty()) {
      return@transaction 0
    }
    WordTagsTable.deleteWhere {
      (WordTagsTable.WtWoID inList termIds) and (WordTagsTable.WtTgID inList tagIds)
    }
  }

  override fun getTagsForTerm(termId: Long): List<Tag> = transaction {
    TagsTable.innerJoin(WordTagsTable, { TgID }, { WtTgID })
        .selectAll()
        .where { WordTagsTable.WtWoID eq termId }
        .map { it.toTag() }
  }

  override fun getTagsForTerms(termIds: List<Long>): Map<Long, List<Tag>> = transaction {
    if (termIds.isEmpty()) {
      emptyMap()
    } else {
      val rows =
          TagsTable.innerJoin(WordTagsTable, { TgID }, { WtTgID }).selectAll().where {
            WordTagsTable.WtWoID inList termIds
          }
      val result = mutableMapOf<Long, MutableList<Tag>>()
      for (row in rows) {
        val termId = row[WordTagsTable.WtWoID]
        val tag = row.toTag()
        result.getOrPut(termId) { mutableListOf() }.add(tag)
      }
      result
    }
  }

  override fun getTagsForBooks(bookIds: List<Long>): Map<Long, List<Tag>> = transaction {
    if (bookIds.isEmpty()) {
      emptyMap()
    } else {
      val rows =
          Tags2Table.innerJoin(BookTagsTable, { T2ID }, { BtT2ID }).selectAll().where {
            BookTagsTable.BtBkID inList bookIds
          }
      val result = mutableMapOf<Long, MutableList<Tag>>()
      for (row in rows) {
        val bookId = row[BookTagsTable.BtBkID]
        val tag = row.toBookTag()
        result.getOrPut(bookId) { mutableListOf() }.add(tag)
      }
      result
    }
  }
}
