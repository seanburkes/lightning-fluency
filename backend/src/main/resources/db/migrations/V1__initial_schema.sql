-- V1: Initial schema for Lightning Fluency (compatible with Lute v3)
-- Creates all 20 tables with indexes, foreign keys, and seed data.
-- PRAGMA foreign_keys=ON is set at connection level by DatabaseFactory.

-- Core tables

CREATE TABLE IF NOT EXISTS languages (
  LgID INTEGER PRIMARY KEY AUTOINCREMENT,
  LgName VARCHAR(40) NOT NULL,
  LgCharacterSubstitutions VARCHAR(500),
  LgRegexpSplitSentences VARCHAR(500),
  LgExceptionsSplitSentences VARCHAR(500),
  LgRegexpWordCharacters VARCHAR(500),
  LgRightToLeft INTEGER NOT NULL DEFAULT 0,
  LgShowRomanization INTEGER NOT NULL DEFAULT 0,
  LgParserType VARCHAR(20) NOT NULL DEFAULT 'spacedel'
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_languages_name ON languages (LgName);

CREATE TABLE IF NOT EXISTS books (
  BkID INTEGER PRIMARY KEY AUTOINCREMENT,
  BkLgID INTEGER NOT NULL REFERENCES languages(LgID),
  BkTitle VARCHAR(200) NOT NULL,
  BkSourceURI VARCHAR(1000),
  BkArchived INTEGER NOT NULL DEFAULT 0,
  BkCurrentTxID INTEGER NOT NULL DEFAULT 0,
  BkAudioFilename TEXT,
  BkAudioCurrentPos REAL,
  BkAudioBookmarks TEXT
);
CREATE INDEX IF NOT EXISTS idx_books_lgid ON books (BkLgID);
CREATE INDEX IF NOT EXISTS idx_books_lgid_archived ON books (BkLgID, BkArchived);

CREATE TABLE IF NOT EXISTS texts (
  TxID INTEGER PRIMARY KEY AUTOINCREMENT,
  TxBkID INTEGER NOT NULL REFERENCES books(BkID),
  TxOrder INTEGER NOT NULL,
  TxText TEXT NOT NULL,
  TxReadDate DATETIME,
  TxWordCount INTEGER,
  TxStartDate DATETIME
);
CREATE INDEX IF NOT EXISTS idx_texts_book_order ON texts (TxBkID, TxOrder);

CREATE TABLE IF NOT EXISTS words (
  WoID INTEGER PRIMARY KEY AUTOINCREMENT,
  WoLgID INTEGER NOT NULL REFERENCES languages(LgID),
  WoText VARCHAR(250) NOT NULL,
  WoTextLC VARCHAR(250) NOT NULL,
  WoStatus INTEGER NOT NULL DEFAULT 0,
  WoTranslation VARCHAR(500),
  WoRomanization VARCHAR(100),
  WoTokenCount INTEGER NOT NULL DEFAULT 1,
  WoCreated DATETIME NOT NULL,
  WoStatusChanged DATETIME NOT NULL,
  WoSyncStatus INTEGER NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_words_lgid ON words (WoLgID);
CREATE INDEX IF NOT EXISTS idx_words_status ON words (WoStatus);
CREATE INDEX IF NOT EXISTS idx_words_status_changed ON words (WoStatusChanged);
CREATE INDEX IF NOT EXISTS idx_words_textlc ON words (WoTextLC);
CREATE UNIQUE INDEX IF NOT EXISTS idx_words_textlc_lgid ON words (WoTextLC, WoLgID);
CREATE INDEX IF NOT EXISTS idx_words_lgid_status_textlc ON words (WoLgID, WoStatus, WoTextLC);

CREATE TABLE IF NOT EXISTS statuses (
  StID INTEGER PRIMARY KEY AUTOINCREMENT,
  StText VARCHAR(20) NOT NULL,
  StAbbreviation VARCHAR(5) NOT NULL
);

-- Tag tables

CREATE TABLE IF NOT EXISTS tags (
  TgID INTEGER PRIMARY KEY AUTOINCREMENT,
  TgText VARCHAR(20) NOT NULL,
  TgComment VARCHAR(200) NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_tags_text ON tags (TgText);

CREATE TABLE IF NOT EXISTS tags2 (
  T2ID INTEGER PRIMARY KEY AUTOINCREMENT,
  T2Text VARCHAR(20) NOT NULL,
  T2Comment VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS wordtags (
  WtWoID INTEGER NOT NULL REFERENCES words(WoID),
  WtTgID INTEGER NOT NULL REFERENCES tags(TgID),
  PRIMARY KEY (WtWoID, WtTgID)
);

CREATE TABLE IF NOT EXISTS booktags (
  BtBkID INTEGER NOT NULL REFERENCES books(BkID),
  BtT2ID INTEGER NOT NULL REFERENCES tags2(T2ID),
  PRIMARY KEY (BtBkID, BtT2ID)
);

-- Relationship tables

CREATE TABLE IF NOT EXISTS wordparents (
  WpWoID INTEGER NOT NULL REFERENCES words(WoID),
  WpParentWoID INTEGER NOT NULL REFERENCES words(WoID),
  PRIMARY KEY (WpWoID, WpParentWoID)
);

CREATE TABLE IF NOT EXISTS sentences (
  SeID INTEGER PRIMARY KEY AUTOINCREMENT,
  SeTxID INTEGER NOT NULL REFERENCES texts(TxID),
  SeOrder INTEGER NOT NULL,
  SeText TEXT,
  SeTextLC TEXT
);

-- Supporting tables

CREATE TABLE IF NOT EXISTS settings (
  StKey VARCHAR(40) PRIMARY KEY,
  StKeyType TEXT NOT NULL,
  StValue TEXT
);

CREATE TABLE IF NOT EXISTS bookstats (
  BkID INTEGER PRIMARY KEY REFERENCES books(BkID),
  distinctterms INTEGER,
  distinctunknowns INTEGER,
  unknownpercent INTEGER,
  status_distribution VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS wordimages (
  WiID INTEGER PRIMARY KEY AUTOINCREMENT,
  WiWoID INTEGER NOT NULL REFERENCES words(WoID),
  WiSource VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS wordflashmessages (
  WfID INTEGER PRIMARY KEY AUTOINCREMENT,
  WfWoID INTEGER NOT NULL REFERENCES words(WoID),
  WfMessage VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS wordsread (
  WrID INTEGER PRIMARY KEY AUTOINCREMENT,
  WrLgID INTEGER NOT NULL REFERENCES languages(LgID),
  WrTxID INTEGER,
  WrReadDate DATETIME NOT NULL,
  WrWordCount INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS srsexportspecs (
  SrsID INTEGER PRIMARY KEY AUTOINCREMENT,
  SrsExportName VARCHAR(200) NOT NULL UNIQUE,
  SrsCriteria VARCHAR(1000) NOT NULL,
  SrsDeckName VARCHAR(200) NOT NULL,
  SrsNoteType VARCHAR(200) NOT NULL,
  SrsFieldMapping VARCHAR(1000) NOT NULL,
  SrsActive INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS textbookmarks (
  TbID INTEGER PRIMARY KEY AUTOINCREMENT,
  TbTxID INTEGER NOT NULL REFERENCES texts(TxID),
  TbTitle TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS languagedicts (
  LdID INTEGER PRIMARY KEY AUTOINCREMENT,
  LdLgID INTEGER NOT NULL REFERENCES languages(LgID),
  LdUseFor VARCHAR(20) NOT NULL,
  LdType VARCHAR(20) NOT NULL,
  LdDictURI VARCHAR(200) NOT NULL,
  LdIsActive INTEGER NOT NULL DEFAULT 1,
  LdSortOrder INTEGER NOT NULL
);

-- Migration tracking table

CREATE TABLE IF NOT EXISTS _migrations (
  migration_name VARCHAR(255) PRIMARY KEY,
  applied_at BIGINT NOT NULL
);

-- Seed data: statuses

INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (0, 'Unknown', '?');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (1, 'New (1)', '1');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (2, 'New (2)', '2');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (3, 'Learning (3)', '3');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (4, 'Learning (4)', '4');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (5, 'Learned', '5');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (99, 'Well Known', 'WKn');
INSERT INTO statuses (StID, StText, StAbbreviation) VALUES (98, 'Ignored', 'Ign');

-- Seed data: default settings

INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('IsDemoData', 'bool', '0');
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('LastBackupDatetime', 'str', NULL);
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('BackupCount', 'int', '5');
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('BackupDir', 'str', NULL);
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('BackupAuto', 'bool', '1');
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('BackupWarn', 'bool', '1');
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('MecabPath', 'str', NULL);
INSERT INTO settings (StKey, StKeyType, StValue) VALUES ('UserThemeCSS', 'str', NULL);
