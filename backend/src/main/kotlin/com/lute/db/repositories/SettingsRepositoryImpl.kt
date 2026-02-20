package com.lute.db.repositories

import com.lute.db.Mappers.toSetting
import com.lute.db.tables.SettingsTable
import com.lute.domain.Setting
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class SettingsRepositoryImpl : SettingsRepository {
  override fun get(key: String): String? = transaction {
    SettingsTable.selectAll()
        .where { SettingsTable.StKey eq key }
        .singleOrNull()
        ?.get(SettingsTable.StValue)
  }

  override fun set(key: String, value: String, keyType: String): Unit = transaction {
    SettingsTable.upsert(SettingsTable.StKey) {
      it[StKey] = key
      it[StValue] = value
      it[StKeyType] = keyType
    }
  }

  override fun getAll(): Map<String, Setting> = transaction {
    SettingsTable.selectAll().associate { row ->
      val setting = row.toSetting()
      setting.key to setting
    }
  }
}
