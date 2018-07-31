package pk.lucidxpo.ynami.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pk.lucidxpo.ynami.persistence.dao.SampleRepository;
import pk.lucidxpo.ynami.persistence.model.Sample;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.valueOf;

@Service
public class SampleServiceImpl implements SampleService {
    @Autowired
    private SampleRepository sampleRepository;

    @Override
    public List<Sample> getAll() {
        return sampleRepository.findAll();
    }

    @Override
    public Optional<Sample> findById(Long id) {
        return sampleRepository.findById(id);
    }

    @Override
    public boolean existsByFirstName(String firstName) {
        return sampleRepository.existsByFirstName(firstName);
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
        sampleRepository.deleteById(id);
    }

    @Override
    public Sample updateStatus(Long id, Map<String, Object> updates) {
        final Optional<Sample> sample = findById(id);

        //potential NullPointerException
        sample.get().setActive(valueOf((String) updates.get("active")));

        return sampleRepository.save(sample.get());
    }
}
