package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("TagRepository Integration Tests")
class TagRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    private Tag testTag;

    @BeforeEach
    void setUp() {
        testTag = TestDataFactory.defaultTag()
                .name("Sporty")
                .build();

        entityManager.persist(testTag);

        entityManager.flush();
    }


    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("returns tag when exists")
        void findById_ExistingTag_ReturnsTag() {
            Optional<Tag> result = tagRepository.findById(testTag.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Sporty");
        }

        @Test
        @DisplayName("returns empty when tag does not exist")
        void findById_NonExistingTag_ReturnsEmpty() {
            Optional<Tag> result = tagRepository.findById(UUID.randomUUID());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("returns all tags")
        void findAll_MultipleTags_ReturnsAllTags() {
            Tag tag2 = TestDataFactory.createTag("Comfortable");
            Tag tag3 = TestDataFactory.createTag("Economical");

            entityManager.persist(tag2);
            entityManager.persist(tag3);
            entityManager.flush();

            List<Tag> result = tagRepository.findAll();
            assertThat(result).hasSize(3);
            assertThat(result).extracting(Tag::getName)
                    .containsExactlyInAnyOrder("Sporty", "Comfortable", "Economical");
        }
    }


    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("persists new tag")
        void save_NewTag_PersistsTag() {
            Tag newTag = TestDataFactory.defaultTag()
                    .name("Family-friendly")
                    .build();

            Tag saved = tagRepository.save(newTag);

            entityManager.flush();

            assertThat(saved.getId()).isNotNull();

            Tag found = entityManager.find(Tag.class, saved.getId());

            assertThat(found.getName()).isEqualTo("Family-friendly");
        }

        @Test
        @DisplayName("updates existing tag")
        void save_ExistingTag_UpdatesTag() {
            testTag.setName("Super Sporty");

            tagRepository.save(testTag);

            entityManager.flush();
            entityManager.clear();

            Tag found = entityManager.find(Tag.class, testTag.getId());

            assertThat(found.getName()).isEqualTo("Super Sporty");
        }
    }


    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("removes tag")
        void delete_ExistingTag_RemovesTag() {
            tagRepository.delete(testTag);
            entityManager.flush();

            Tag found = entityManager.find(Tag.class, testTag.getId());

            assertThat(found).isNull();
        }
    }


    @Nested
    @DisplayName("count Tests")
    class CountTests {

        @Test
        @DisplayName("returns correct number")
        void count_MultipleTags_ReturnsCorrectCount() {
            Tag tag2 = TestDataFactory.createTag("Comfortable");

            entityManager.persist(tag2);
            entityManager.flush();

            long count = tagRepository.count();

            assertThat(count).isEqualTo(2);
        }
    }


    @Nested
    @DisplayName("findAllById Tests")
    class FindAllByIdTests {

        @Test
        @DisplayName("returns matching tags")
        void findAllById_MultipleIds_ReturnsMatchingTags() {
            Tag tag2 = TestDataFactory.createTag("Comfortable");
            Tag tag3 = TestDataFactory.createTag("Economical");

            entityManager.persist(tag2);
            entityManager.persist(tag3);

            entityManager.flush();

            List<Tag> result = tagRepository.findAllById(List.of(testTag.getId(), tag2.getId()));
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Tag::getName)
                    .containsExactlyInAnyOrder("Sporty", "Comfortable");
        }
    }
}
