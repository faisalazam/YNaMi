package pk.lucidxpo.ynami.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.lucidxpo.ynami.persistence.model.AuditEntryArchive;

import java.util.List;

@Repository
public interface AuditEntryArchiveRepository extends JpaRepository<AuditEntryArchive, String> {
    List<AuditEntryArchive> findByChangedEntityIdOrderByChangedAtDesc(String changedEntityId);
}