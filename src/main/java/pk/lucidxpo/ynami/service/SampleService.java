package pk.lucidxpo.ynami.service;

import pk.lucidxpo.ynami.persistence.model.Sample;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SampleService {
    List<Sample> getAll();

    boolean exists(Long id);

    Optional<Sample> findById(Long id);

    Sample create(Sample sample);

    Sample update(Sample sample);

    void delete(Long id);

    Sample updateStatus(Long id, Map<String, Object> updates);
}