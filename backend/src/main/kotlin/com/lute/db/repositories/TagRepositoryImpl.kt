package com.lute.db.repositories

import com.lute.db.Mappers.toTag
import com.lute.db.tables.TagsTable
import com.lute.db.tables.WordTagsTable
import com.lute.domain.Tag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TagRepositoryImpl : TagRepository {
  override fun findAll(): List<Tag> = transaction { TagsTable.selectAll().map { it.toTag() } }

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
    WordTagsTable.insert {
      it[WtWoID] = termId
      it[WtTgID] = tagId
    }
  }

  override fun removeTagFromTerm(termId: Long, tagId: Long): Unit = transaction {
    WordTagsTable.deleteWhere { (WtWoID eq termId) and (WtTgID eq tagId) }
  }

  override fun getTagsForTerm(termId: Long): List<Tag> = transaction {
    TagsTable.innerJoin(WordTagsTable, { TgID }, { WtTgID })
        .selectAll()
        .where { WordTagsTable.WtWoID eq termId }
        .map { it.toTag() }
  }
}
