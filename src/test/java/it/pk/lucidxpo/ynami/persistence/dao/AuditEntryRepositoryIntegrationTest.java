package it.pk.lucidxpo.ynami.persistence.dao;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import pk.lucidxpo.ynami.persistence.dao.AuditEntryRepository;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static pk.lucidxpo.ynami.utils.Identity.randomID;
import static pk.lucidxpo.ynami.utils.matchers.ObjectDeepDetailMatcher.equivalentTo;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class AuditEntryRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuditEntryRepository repository;

    @Test
    void shouldSaveAuditEntryIntoDatabase() {
        final AuditEntry saved = new AuditEntry("TestEntity", "testId",
                "testField", "foo", "bar", "tester");
        repository.save(saved);

        final AuditEntry found = repository.findById(saved.getId()).get();

        assertAll(
                () -> assertEquals(saved.getChangedEntityName(), found.getChangedEntityName()),
                () -> assertEquals(saved.getChangedEntityId(), found.getChangedEntityId()),

                () -> assertEquals(saved.getFieldChanged(), found.getFieldChanged()),
                () -> assertEquals(saved.getFromValue(), found.getFromValue()),
                () -> assertEquals(saved.getToValue(), found.getToValue()),

                () -> assertEquals(saved.getChangedBy(), found.getChangedBy()),
                () -> assertEquals(saved.getChangedAt(), found.getChangedAt())
        );
    }

    @Test
    void shouldRetrieveAuditEntryRecordsByEntityId() {

        final String matchingEntityId = randomID();
        final String nonMatchingEntityId = randomID();

        final AuditEntry auditEntry1 = new AuditEntry(randomID(), matchingEntityId, "testField1",
                "fromValue1", "toValue1", "changedBy1");
        final AuditEntry auditEntry2 = new AuditEntry(randomID(), matchingEntityId, "testField2",
                "fromValue2", "toValue2", "changedBy2");
        final AuditEntry auditEntry3 = new AuditEntry(randomID(), nonMatchingEntityId, "testField3",
                "fromValue3", "toValue3", "changedBy3");

        repository.save(auditEntry1);
        repository.save(auditEntry2);
        repository.save(auditEntry3);

        List<AuditEntry> records = repository.findByChangedEntityIdOrderByChangedAtDesc(matchingEntityId);
        assertEquals(2, records.size());
        assertThat(auditEntry2, equivalentTo(records.get(0)));
        assertThat(auditEntry1, equivalentTo(records.get(1)));

        records = repository.findByChangedEntityIdOrderByChangedAtDesc(nonMatchingEntityId);
        assertEquals(1, records.size());
        assertThat(auditEntry3, equivalentTo(records.get(0)));

        records = repository.findByChangedEntityIdOrderByChangedAtDesc(randomID());
        assertTrue(records.isEmpty());
    }

    @Test
    void shouldRetrieveAuditEntryOlderThanDate() {

        final AuditEntry auditEntry1 = createAuditEntry("1");
        final AuditEntry auditEntry2 = createAuditEntry("2");
        final AuditEntry auditEntry3 = createAuditEntry("3");

        updateAuditEntryChangedAtDate(now().minusDays(1), auditEntry1);
        updateAuditEntryChangedAtDate(now().minusDays(2), auditEntry2);
        updateAuditEntryChangedAtDate(now().minusDays(3), auditEntry3);

        repository.save(auditEntry1);
        repository.save(auditEntry2);
        repository.save(auditEntry3);

        List<AuditEntry> messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now().minusDays(3), of(0, 10));
        assertEquals(1, messages.size());
        assertEquals(auditEntry3.getId(), messages.get(0).getId());

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now().minusDays(2), of(0, 10));
        assertEquals(2, messages.size());
        assertEquals(auditEntry3.getId(), messages.get(0).getId());
        assertEquals(auditEntry2.getId(), messages.get(1).getId());

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now().minusDays(1), of(0, 10));
        assertEquals(3, messages.size());
        assertEquals(auditEntry3.getId(), messages.get(0).getId());
        assertEquals(auditEntry2.getId(), messages.get(1).getId());
        assertEquals(auditEntry1.getId(), messages.get(2).getId());
    }

    @Test
    void shouldRetrieveAuditEntryByMaxResults() {

        final AuditEntry auditEntry1 = createAuditEntry("1");
        final AuditEntry auditEntry2 = createAuditEntry("2");
        final AuditEntry auditEntry3 = createAuditEntry("3");
        final AuditEntry auditEntry4 = createAuditEntry("4");
        final AuditEntry auditEntry5 = createAuditEntry("5");

        repository.save(auditEntry1);
        repository.save(auditEntry2);
        repository.save(auditEntry3);
        repository.save(auditEntry4);
        repository.save(auditEntry5);

        List<AuditEntry> messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 5));
        assertEquals(5, messages.size());

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 4));
        assertEquals(4, messages.size());

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 3));
        assertEquals(3, messages.size());

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 2));
        assertEquals(2, messages.size());

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 1));
        assertEquals(1, messages.size());
    }

    @Test
    void shouldSaveLargeTextValueAuditEntryIntoDatabase() {
        final AuditEntry saved = new AuditEntry("TestEntity", "testId",
                "testField", repeat("A", 4000), repeat("B", 4000), "tester");
        repository.save(saved);

        final AuditEntry found = repository.findById(saved.getId()).get();
        assertNotNull(found);
    }

    private void updateAuditEntryChangedAtDate(final LocalDateTime dateTime, final AuditEntry auditEntry) {
        setField(auditEntry, "changedAt", dateTime);
    }

    private AuditEntry createAuditEntry(final String number) {
        return new AuditEntry("changedEntityName" + number, "changedEntityId" + number,
                "fieldChanged" + number, "fromValue" + number, "toValue" + number,
                "changedBy");
    }
}