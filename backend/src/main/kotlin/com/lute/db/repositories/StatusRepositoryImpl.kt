package com.lute.db.repositories

import com.lute.db.Mappers.toStatus
import com.lute.db.tables.StatusesTable
import com.lute.domain.Status
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class StatusRepositoryImpl : StatusRepository {
  override fun findAll(): List<Status> = transaction {
    StatusesTable.selectAll().map { it.toStatus() }
  }

  override fun findById(id: Long): Status? = transaction {
    StatusesTable.selectAll().where { StatusesTable.StID eq id }.singleOrNull()?.toStatus()
  }

  override fun save(status: Status): Long = transaction {
    StatusesTable.insert {
          it[StText] = status.text
          it[StAbbreviation] = status.abbreviation
        }[StatusesTable.StID]
  }
}
