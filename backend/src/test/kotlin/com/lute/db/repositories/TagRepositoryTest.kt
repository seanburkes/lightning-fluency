package com.lute.db.repositories

import com.lute.db.DatabaseTestBase
import com.lute.domain.Language
import com.lute.domain.Tag
import com.lute.domain.Term
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TagRepositoryTest : DatabaseTestBase() {
  private val langRepo = LanguageRepository()
  private val termRepo = TermRepository()
  private val repo = TagRepository()
  private var langId: Long = 0

  @BeforeEach
  fun seedLanguage() {
    langId = langRepo.save(Language(name = "English"))
  }

  @Test
  fun `save returns generated id`() {
    val id = repo.save(Tag(text = "noun", comment = "nouns"))
    assertTrue(id > 0)
  }

  @Test
  fun `findAll returns all tags`() {
    repo.save(Tag(text = "verb", comment = "verbs"))
    repo.save(Tag(text = "noun", comment = "nouns"))
    assertEquals(2, repo.findAll().size)
  }

  @Test
  fun `findByText returns matching tag`() {
    repo.save(Tag(text = "adjective", comment = "adj"))
    val found = repo.findByText("adjective")
    assertNotNull(found)
    assertEquals("adjective", found.text)
    assertEquals("adj", found.comment)
  }

  @Test
  fun `findByText returns null when not found`() {
    assertNull(repo.findByText("nonexistent"))
  }

  @Test
  fun `addTagToTerm and getTagsForTerm work together`() {
    val tagId = repo.save(Tag(text = "important", comment = ""))
    val termId = termRepo.save(Term(languageId = langId, text = "Hello", textLC = "hello"))
    repo.addTagToTerm(termId, tagId)
    val tags = repo.getTagsForTerm(termId)
    assertEquals(1, tags.size)
    assertEquals("important", tags[0].text)
  }

  @Test
  fun `getTagsForTerm returns empty list when no tags`() {
    val termId = termRepo.save(Term(languageId = langId, text = "Lonely", textLC = "lonely"))
    assertEquals(0, repo.getTagsForTerm(termId).size)
  }

  @Test
  fun `removeTagFromTerm removes association`() {
    val tagId = repo.save(Tag(text = "temp", comment = ""))
    val termId = termRepo.save(Term(languageId = langId, text = "Word", textLC = "word"))
    repo.addTagToTerm(termId, tagId)
    assertEquals(1, repo.getTagsForTerm(termId).size)
    repo.removeTagFromTerm(termId, tagId)
    assertEquals(0, repo.getTagsForTerm(termId).size)
  }

  @Test
  fun `term can have multiple tags`() {
    val tag1 = repo.save(Tag(text = "tag1", comment = ""))
    val tag2 = repo.save(Tag(text = "tag2", comment = ""))
    val termId = termRepo.save(Term(languageId = langId, text = "Multi", textLC = "multi"))
    repo.addTagToTerm(termId, tag1)
    repo.addTagToTerm(termId, tag2)
    assertEquals(2, repo.getTagsForTerm(termId).size)
  }
}
