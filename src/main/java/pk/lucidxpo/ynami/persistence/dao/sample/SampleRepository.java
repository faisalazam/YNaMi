package pk.lucidxpo.ynami.persistence.dao.sample;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pk.lucidxpo.ynami.persistence.model.sample.Sample;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    boolean existsByFirstName(String firstName);
}