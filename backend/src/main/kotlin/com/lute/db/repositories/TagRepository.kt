package com.lute.db.repositories

import com.lute.db.Mappers.toTag
import com.lute.db.tables.TagsTable
import com.lute.db.tables.WordTagsTable
import com.lute.domain.Tag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TagRepository {
  fun findAll(): List<Tag> = transaction { TagsTable.selectAll().map { it.toTag() } }

  fun findByText(text: String): Tag? = transaction {
    TagsTable.selectAll().where { TagsTable.TgText eq text }.singleOrNull()?.toTag()
  }

  fun save(tag: Tag): Int = transaction {
    TagsTable.insert {
          it[TgText] = tag.text
          it[TgComment] = tag.comment
        }[TagsTable.TgID]
  }

  fun addTagToTerm(termId: Int, tagId: Int): Unit = transaction {
    WordTagsTable.insert {
      it[WtWoID] = termId
      it[WtTgID] = tagId
    }
  }

  fun removeTagFromTerm(termId: Int, tagId: Int): Unit = transaction {
    WordTagsTable.deleteWhere { (WtWoID eq termId) and (WtTgID eq tagId) }
  }

  fun getTagsForTerm(termId: Int): List<Tag> = transaction {
    TagsTable.innerJoin(WordTagsTable, { TgID }, { WtTgID })
        .selectAll()
        .where { WordTagsTable.WtWoID eq termId }
        .map { it.toTag() }
  }
}
