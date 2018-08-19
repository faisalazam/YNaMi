package pk.lucidxpo.ynami.service.sample;

import pk.lucidxpo.ynami.persistence.model.sample.Sample;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SampleService {
    List<Sample> getAll();

    boolean existsByFirstName(String firstName);

    Optional<Sample> findById(String id);

    Sample create(Sample sample);

    Sample update(Sample sample);

    void delete(String id);

    Sample updateStatus(String id, Map<String, Object> updates);
}