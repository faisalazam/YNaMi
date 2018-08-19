package pk.lucidxpo.ynami.persistence.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.lucidxpo.ynami.persistence.model.AuditEntry;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, String> {
    List<AuditEntry> findByChangedEntityIdOrderByChangedAtDesc(String changedEntityId);

    List<AuditEntry> findByChangedAtLessThanEqualOrderByChangedAtAsc(LocalDateTime changedAt, Pageable pageable);
}