package pk.lucidxpo.ynami.persistence.model.sample;

import pk.lucidxpo.ynami.persistence.model.AuditableBuilder;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

public final class SampleBuilder extends AuditableBuilder<Sample, SampleBuilder> {
    private String firstName = randomAlphabetic(5, 50);
    private String lastName = randomAlphabetic(5, 50);
    private String address = randomAlphabetic(5, 50);
    private boolean active = chooseOneOf(true, false);

    private SampleBuilder() {
    }

    public static SampleBuilder aSample() {
        return new SampleBuilder();
    }

    public SampleBuilder withFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public SampleBuilder withLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public SampleBuilder withAddress(final String address) {
        this.address = address;
        return this;
    }

    public SampleBuilder withActive(final boolean active) {
        this.active = active;
        return this;
    }

    @Override
    public Sample build() {
        final Sample sample = new Sample();
        sample.setId(id);
        sample.setCreatedBy(createdBy);
        sample.setCreatedDate(createdDate);
        sample.setLastModifiedBy(lastModifiedBy);
        sample.setLastModifiedDate(lastModifiedDate);

        sample.setActive(active);
        sample.setAddress(address);
        sample.setLastName(lastName);
        sample.setFirstName(firstName);
        return sample;
    }
}
