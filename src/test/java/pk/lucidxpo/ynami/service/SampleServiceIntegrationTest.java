package pk.lucidxpo.ynami.service;

import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.dao.SampleRepository;
import pk.lucidxpo.ynami.persistence.model.Sample;
import pk.lucidxpo.ynami.testutils.ObjectDeepDetailMatcher;

import java.util.List;
import java.util.Map;

import static java.lang.Long.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertThat;
import static pk.lucidxpo.ynami.persistence.model.Sample.builder;
import static pk.lucidxpo.ynami.testutils.Identity.randomInt;
import static pk.lucidxpo.ynami.testutils.Randomly.chooseOneOf;

public class SampleServiceIntegrationTest extends AbstractIntegrationTest {
    private static final long FROZEN_TIME = new LocalDateTime().toDateTime().getMillis();

    @Autowired
    private SampleService sampleService;

    @Autowired
    private SampleRepository sampleRepository;

    private Sample sample1;
    private Sample sample2;
    private Sample sample3;
    private Sample sample4;
    private Sample sample5;

    @Before
    public void setup() {
        sampleRepository.deleteAll();

        sample1 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample2 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample3 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample4 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample5 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
    }

    @Before
    public void freezeTime() {
        setCurrentMillisFixed(FROZEN_TIME);
    }

    @After
    public void unFreezeTime() {
        setCurrentMillisSystem();
    }

    @Test
    public void shouldVerifyThatAuditInfoIsStored() throws Exception {
        assertThat(sample1.getCreatedDate(), nullValue());
        assertThat(sample1.getLastModifiedDate(), nullValue());
        assertThat(sample1.getLastModifiedBy(), nullValue());
        assertThat(sample1.getCreatedBy(), nullValue());

        final Sample savedSample = sampleRepository.save(sample1);

        assertThat(savedSample.getCreatedDate().toString(), containsString(now().toString()));
        assertThat(savedSample.getLastModifiedDate().toString(), containsString(now().toString()));
        assertThat(savedSample.getLastModifiedBy(), is("Crazy"));
        assertThat(savedSample.getCreatedBy(), is("Crazy"));
    }

    @Test
    public void shouldVerifyThatAllElementsAreReturnedOnGetAll() throws Exception {
        final List<Sample> expectedSamples = asList(
                sampleRepository.save(sample1),
                sampleRepository.save(sample2),
                sampleRepository.save(sample3),
                sampleRepository.save(sample4),
                sampleRepository.save(sample5)
        );

        final List<Sample> actualSamples = sampleService.getAll();

        assertThat(actualSamples.size(), is(expectedSamples.size()));
        assertThat(actualSamples, containsInAnyOrder(expectedSamples.toArray()));
    }

    @Test
    public void shouldVerifyThatNoElementIsReturnedOnGetAllWhenThereIsNoElement() throws Exception {
        final List<Sample> actualSamples = sampleService.getAll();

        assertThat(actualSamples.isEmpty(), is(true));
    }

    @Test
    public void shouldVerifyTheRetrievalOfElementById() throws Exception {
        sample1 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        final Sample savedSample = sampleRepository.save(sample1);

        assertThat(sampleService.findById(savedSample.getId()).get(), new ObjectDeepDetailMatcher(savedSample));
        assertThat(sampleService.findById(valueOf(randomInt())).isPresent(), is(false));
    }

    @Test
    public void shouldVerifyTheExistenceOfElement() throws Exception {
        sample1 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sampleRepository.save(sample1);

        assertThat(sampleService.existsByFirstName(sample1.getFirstName()), is(true));
        assertThat(sampleService.existsByFirstName(sample2.getFirstName()), is(false));
        assertThat(sampleService.existsByFirstName(sample3.getFirstName()), is(false));
    }

    @Test
    public void create() throws Exception {
        sample1 = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        final Sample createdSample = sampleService.create(sample1);

        final Sample actualSample = sampleService.findById(createdSample.getId()).get();
        assertThat(actualSample.isActive(), is(sample1.isActive()));
        assertThat(actualSample.getAddress(), is(sample1.getAddress()));
        assertThat(actualSample.getFirstName(), is(sample1.getFirstName()));
        assertThat(actualSample.getLastName(), is(sample1.getLastName()));
    }

    @Test
    public void update() throws Exception {
        sample1 = builder()
                .active(true)
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        final Sample createdSample = sampleService.create(sample1);

        final Sample actualCreatedSample = sampleService.findById(createdSample.getId()).get();
        assertThat(actualCreatedSample.isActive(), is(sample1.isActive()));
        assertThat(actualCreatedSample.getAddress(), is(sample1.getAddress()));
        assertThat(actualCreatedSample.getFirstName(), is(sample1.getFirstName()));
        assertThat(actualCreatedSample.getLastName(), is(sample1.getLastName()));

        sample1.setActive(false);
        sample1.setAddress("Dummy Address");
        sample1.setFirstName("First Name");
        sample1.setLastName("Last Name");
        sampleService.update(sample1);

        final Sample actualUpdatedSample = sampleService.findById(createdSample.getId()).get();
        assertThat(actualUpdatedSample.isActive(), is(false));
        assertThat(actualUpdatedSample.getAddress(), is("Dummy Address"));
        assertThat(actualUpdatedSample.getFirstName(), is("First Name"));
        assertThat(actualUpdatedSample.getLastName(), is("Last Name"));
    }

    @Test
    public void shouldVerifyTheDeletionOfElement() throws Exception {
        sample1 = sampleRepository.save(sample1);

        assertThat(sampleService.findById(sample1.getId()).isPresent(), is(true));

        sampleService.delete(sample1.getId());

        assertThat(sampleService.findById(sample1.getId()).isPresent(), is(false));
    }

    @Test
    public void shouldVerifyThatOnlyStatusIsUpdatedOnUpdateStatusWhenElementSpecifiedByIdIsFound() throws Exception {
        final Map<String, Object> updates = newHashMap();
        updates.put("id", "12");
        updates.put("firstName", "test first");
        updates.put("lastName", "test last");
        updates.put("address", "test address");
        updates.put("active", "true");

        sample1.setActive(false);
        final Sample savedSample = sampleRepository.save(sample1);

        assertThat(sampleService.findById(savedSample.getId()).get().isActive(), is(false));

        final Sample actualService = sampleService.updateStatus(savedSample.getId(), updates);

        assertThat(actualService.isActive(), is(true));
        assertThat(actualService.getId(), is(savedSample.getId()));
        assertThat(actualService.getFirstName(), is(sample1.getFirstName()));
        assertThat(actualService.getLastName(), is(sample1.getLastName()));
        assertThat(actualService.getAddress(), is(sample1.getAddress()));

        assertThat(actualService.getLastModifiedDate().isAfter(savedSample.getLastModifiedDate()), is(true));
    }
}