package it.pk.lucidxpo.ynami.persistence.dao;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pk.lucidxpo.ynami.persistence.dao.AuditEntryArchiveRepository;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;
import pk.lucidxpo.ynami.persistence.model.AuditEntryArchive;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.beans.BeanUtils.copyProperties;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

class AuditEntryArchiveRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuditEntryArchiveRepository archiveRepository;

    @Test
    void shouldRetrieveAuditEntryArchiveRecordsByEntityId() {

        final String matchingEntityId = randomID();
        final String nonMatchingEntityId = randomID();

        final AuditEntry auditEntry1 = new AuditEntry(randomID(), matchingEntityId, "testField1",
                "fromValue1", "toValue1", "changedBy1");
        final AuditEntryArchive auditEntryArchive1 = new AuditEntryArchive();
        copyProperties(auditEntry1, auditEntryArchive1);

        final AuditEntry auditEntry2 = new AuditEntry(randomID(), matchingEntityId, "testField2",
                "fromValue2", "toValue2", "changedBy2");
        final AuditEntryArchive auditEntryArchive2 = new AuditEntryArchive();
        copyProperties(auditEntry2, auditEntryArchive2);

        final AuditEntry auditEntry3 = new AuditEntry(randomID(), nonMatchingEntityId, "testField3",
                "fromValue3", "toValue3", "changedBy3");
        final AuditEntryArchive auditEntryArchive3 = new AuditEntryArchive();
        copyProperties(auditEntry3, auditEntryArchive3);

        archiveRepository.save(auditEntryArchive1);
        archiveRepository.save(auditEntryArchive2);
        archiveRepository.save(auditEntryArchive3);

        List<AuditEntryArchive> records = archiveRepository.findByChangedEntityIdOrderByChangedAtDesc(matchingEntityId);
        assertEquals(2, records.size());
        assertEntityAttributes(auditEntryArchive2, records.get(0));
        assertEntityAttributes(auditEntryArchive1, records.get(1));

        records = archiveRepository.findByChangedEntityIdOrderByChangedAtDesc(nonMatchingEntityId);
        assertEquals(1, records.size());
        assertEntityAttributes(auditEntryArchive3, records.get(0));

        records = archiveRepository.findByChangedEntityIdOrderByChangedAtDesc(randomID());
        assertTrue(records.isEmpty());
    }

    private void assertEntityAttributes(final AuditEntryArchive expectedAuditEntryArchive, final AuditEntryArchive actualAuditEntryArchive) {
        assertAll(
                () -> assertEquals(expectedAuditEntryArchive.getChangedEntityName(), actualAuditEntryArchive.getChangedEntityName()),
                () -> assertEquals(expectedAuditEntryArchive.getChangedEntityId(), actualAuditEntryArchive.getChangedEntityId()),

                () -> assertEquals(expectedAuditEntryArchive.getFieldChanged(), actualAuditEntryArchive.getFieldChanged()),
                () -> assertEquals(expectedAuditEntryArchive.getFromValue(), actualAuditEntryArchive.getFromValue()),
                () -> assertEquals(expectedAuditEntryArchive.getToValue(), actualAuditEntryArchive.getToValue()),

                () -> assertEquals(expectedAuditEntryArchive.getChangedBy(), actualAuditEntryArchive.getChangedBy()),
                () -> assertEquals(expectedAuditEntryArchive.getChangedAt(), actualAuditEntryArchive.getChangedAt())
        );
    }
}