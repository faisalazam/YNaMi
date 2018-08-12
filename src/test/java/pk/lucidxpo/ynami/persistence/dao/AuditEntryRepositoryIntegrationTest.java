package pk.lucidxpo.ynami.persistence.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;
import pk.lucidxpo.ynami.utils.matchers.ObjectDeepDetailMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

public class AuditEntryRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuditEntryRepository repository;

    @Test
    public void shouldSaveAuditEntryIntoDatabase() {
        final AuditEntry saved = new AuditEntry("TestEntity", "testId", "testField", "foo", "bar", "tester");
        repository.save(saved);

        final AuditEntry found = repository.findById(saved.getId()).get();

        assertThat(found.getChangedEntityName(), equalTo(saved.getChangedEntityName()));
        assertThat(found.getChangedEntityId(), equalTo(saved.getChangedEntityId()));

        assertThat(found.getFieldChanged(), equalTo(saved.getFieldChanged()));
        assertThat(found.getFromValue(), equalTo(saved.getFromValue()));
        assertThat(found.getToValue(), equalTo(saved.getToValue()));

        assertThat(found.getChangedBy(), equalTo(saved.getChangedBy()));
        assertThat(found.getChangedAt(), equalTo(saved.getChangedAt()));
    }

    @Test
    public void shouldRetrieveAuditEntryRecordsByEntityId() {

        final String matchingEntityId = randomID();
        final String nonMatchingEntityId = randomID();

        final AuditEntry auditEntry1 = new AuditEntry(randomID(), matchingEntityId, "testField1", "fromValue1", "toValue1", "changedBy1");
        final AuditEntry auditEntry2 = new AuditEntry(randomID(), matchingEntityId, "testField2", "fromValue2", "toValue2", "changedBy2");
        final AuditEntry auditEntry3 = new AuditEntry(randomID(), nonMatchingEntityId, "testField3", "fromValue3", "toValue3", "changedBy3");

        repository.save(auditEntry1);
        repository.save(auditEntry2);
        repository.save(auditEntry3);

        List<AuditEntry> records = repository.findByChangedEntityIdOrderByChangedAtDesc(matchingEntityId);
        assertThat(records.size(), is(2));
        assertThat(auditEntry2, new ObjectDeepDetailMatcher(records.get(0)));
        assertThat(auditEntry1, new ObjectDeepDetailMatcher(records.get(1)));

        records = repository.findByChangedEntityIdOrderByChangedAtDesc(nonMatchingEntityId);
        assertThat(records.size(), is(1));
        assertThat(auditEntry3, new ObjectDeepDetailMatcher(records.get(0)));

        records = repository.findByChangedEntityIdOrderByChangedAtDesc(randomID());
        assertThat(records.isEmpty(), is(true));
    }

    @Test
    public void shouldRetrieveAuditEntryOlderThanDate() {

        final AuditEntry auditEntry1 = createAuditEntry();
        final AuditEntry auditEntry2 = createAuditEntry();
        final AuditEntry auditEntry3 = createAuditEntry();

        updateAuditEntryChangedAtDate(now().minusDays(1), auditEntry1);
        updateAuditEntryChangedAtDate(now().minusDays(2), auditEntry2);
        updateAuditEntryChangedAtDate(now().minusDays(3), auditEntry3);

        repository.save(auditEntry1);
        repository.save(auditEntry2);
        repository.save(auditEntry3);

        List<AuditEntry> messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now().minusDays(3), of(0, 10));
        assertThat(messages.size(), is(1));
        assertThat(messages.get(0).getId(), is(auditEntry3.getId()));

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now().minusDays(2), of(0, 10));
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0).getId(), is(auditEntry3.getId()));
        assertThat(messages.get(1).getId(), is(auditEntry2.getId()));

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now().minusDays(1), of(0, 10));
        assertThat(messages.size(), is(3));
        assertThat(messages.get(0).getId(), is(auditEntry3.getId()));
        assertThat(messages.get(1).getId(), is(auditEntry2.getId()));
        assertThat(messages.get(2).getId(), is(auditEntry1.getId()));
    }

    @Test
    public void shouldRetrieveAuditEntryByMaxResults() {

        final AuditEntry auditEntry1 = createAuditEntry();
        final AuditEntry auditEntry2 = createAuditEntry();
        final AuditEntry auditEntry3 = createAuditEntry();
        final AuditEntry auditEntry4 = createAuditEntry();
        final AuditEntry auditEntry5 = createAuditEntry();

        repository.save(auditEntry1);
        repository.save(auditEntry2);
        repository.save(auditEntry3);
        repository.save(auditEntry4);
        repository.save(auditEntry5);

        List<AuditEntry> messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 5));
        assertThat(messages.size(), is(5));

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 4));
        assertThat(messages.size(), is(4));

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 3));
        assertThat(messages.size(), is(3));

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 2));
        assertThat(messages.size(), is(2));

        messages = repository.findByChangedAtLessThanEqualOrderByChangedAtAsc(now(), of(0, 1));
        assertThat(messages.size(), is(1));
    }

    @Test
    public void shouldSaveLargeTextValueAuditEntryIntoDatabase() {
        final AuditEntry saved = new AuditEntry("TestEntity", "testId", "testField", repeat("A", 4000), repeat("B", 4000), "tester");
        repository.save(saved);

        final AuditEntry found = repository.findById(saved.getId()).get();
        assertNotNull(found);
    }

    private void updateAuditEntryChangedAtDate(final LocalDateTime dateTime, final AuditEntry auditEntry) {
        setField(auditEntry, "changedAt", dateTime);
    }

    private AuditEntry createAuditEntry() {
        return new AuditEntry("changedEntityName", "changedEntityId", "fieldChanged", "fromValue", "toValue", "changedBy");
    }
}