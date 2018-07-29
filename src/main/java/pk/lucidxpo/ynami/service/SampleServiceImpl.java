package pk.lucidxpo.ynami.service;

import org.springframework.stereotype.Service;
import pk.lucidxpo.ynami.persistence.model.Sample;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

@Service
public class SampleServiceImpl implements SampleService {
    @Override
    public List<Sample> getAll() {
        return emptyList();
    }

    @Override
    public Optional<Sample> findById(Long id) {
        return empty();
    }

    @Override
    public boolean exists(Long id) {
        return false;
    }

    @Override
    public Sample create(Sample sample) {
        return null;
    }

    @Override
    public Sample update(Sample sample) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Sample updateStatus(Long id, Map<String, Object> updates) {
        return null;
    }
}
