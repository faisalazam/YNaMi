package pk.lucidxpo.ynami.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.lucidxpo.ynami.persistence.model.Sample;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
}