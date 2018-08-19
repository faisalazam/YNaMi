package pk.lucidxpo.ynami.service.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.lucidxpo.ynami.persistence.dao.sample.SampleRepository;
import pk.lucidxpo.ynami.persistence.model.sample.Sample;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.valueOf;

@Service
public class SampleServiceImpl implements SampleService {
    private final SampleRepository sampleRepository;

    @Autowired
    public SampleServiceImpl(final SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    @Override
    public List<Sample> getAll() {
        return sampleRepository.findAll();
    }

    @Override
    public Optional<Sample> findById(String id) {
        return sampleRepository.findById(id);
    }

    @Override
    public boolean existsByFirstName(String firstName) {
        return sampleRepository.existsByFirstName(firstName);
    }

    @Override
    public Sample create(Sample sample) {
        return sampleRepository.save(sample);
    }

    @Override
    public Sample update(Sample sample) {
        return sampleRepository.save(sample);
    }

    @Override
    public void delete(String id) {
        sampleRepository.deleteById(id);
    }

    @Override
    public Sample updateStatus(String id, Map<String, Object> updates) {
        final Optional<Sample> sample = findById(id);

        //potential NullPointerException
        sample.get().setActive(valueOf((String) updates.get("active")));

        return sampleRepository.save(sample.get());
    }
}
