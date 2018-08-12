package pk.lucidxpo.ynami.persistence.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;
import pk.lucidxpo.ynami.persistence.model.AuditEntryArchive;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.beans.BeanUtils.copyProperties;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

public class AuditEntryArchiveRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuditEntryArchiveRepository archiveRepository;

    @Test
    public void shouldRetrieveAuditEntryArchiveRecordsByEntityId() {

        final String matchingEntityId = randomID();
        final String nonMatchingEntityId = randomID();

        final AuditEntry auditEntry1 = new AuditEntry(randomID(), matchingEntityId, "testField1", "fromValue1", "toValue1", "changedBy1");
        final AuditEntryArchive auditEntryArchive1 = new AuditEntryArchive();
        copyProperties(auditEntry1, auditEntryArchive1);

        final AuditEntry auditEntry2 = new AuditEntry(randomID(), matchingEntityId, "testField2", "fromValue2", "toValue2", "changedBy2");
        final AuditEntryArchive auditEntryArchive2 = new AuditEntryArchive();
        copyProperties(auditEntry2, auditEntryArchive2);

        final AuditEntry auditEntry3 = new AuditEntry(randomID(), nonMatchingEntityId, "testField3", "fromValue3", "toValue3", "changedBy3");
        final AuditEntryArchive auditEntryArchive3 = new AuditEntryArchive();
        copyProperties(auditEntry3, auditEntryArchive3);

        archiveRepository.save(auditEntryArchive1);
        archiveRepository.save(auditEntryArchive2);
        archiveRepository.save(auditEntryArchive3);

        List<AuditEntryArchive> records = archiveRepository.findByChangedEntityIdOrderByChangedAtDesc(matchingEntityId);
        assertThat(records.size(), is(2));
        assertEntityAttributes(auditEntryArchive2, records.get(0));
        assertEntityAttributes(auditEntryArchive1, records.get(1));

        records = archiveRepository.findByChangedEntityIdOrderByChangedAtDesc(nonMatchingEntityId);
        assertThat(records.size(), is(1));
        assertEntityAttributes(auditEntryArchive3, records.get(0));

        records = archiveRepository.findByChangedEntityIdOrderByChangedAtDesc(randomID());
        assertThat(records.isEmpty(), is(true));
    }

    private void assertEntityAttributes(final AuditEntryArchive expectedAuditEntryArchive, final AuditEntryArchive actualAuditEntryArchive) {

        assertThat(actualAuditEntryArchive.getChangedEntityName(), equalTo(expectedAuditEntryArchive.getChangedEntityName()));
        assertThat(actualAuditEntryArchive.getChangedEntityId(), equalTo(expectedAuditEntryArchive.getChangedEntityId()));

        assertThat(actualAuditEntryArchive.getFieldChanged(), equalTo(expectedAuditEntryArchive.getFieldChanged()));
        assertThat(actualAuditEntryArchive.getFromValue(), equalTo(expectedAuditEntryArchive.getFromValue()));
        assertThat(actualAuditEntryArchive.getToValue(), equalTo(expectedAuditEntryArchive.getToValue()));

        assertThat(actualAuditEntryArchive.getChangedBy(), equalTo(expectedAuditEntryArchive.getChangedBy()));
        assertThat(actualAuditEntryArchive.getChangedAt(), equalTo(expectedAuditEntryArchive.getChangedAt()));
    }
}