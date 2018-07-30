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

        sample1 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        sample2 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        sample3 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        sample4 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        sample5 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
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
        sample1 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        final Sample savedSample = sampleRepository.save(sample1);

        assertThat(sampleService.findById(sample1.getId()).get(), new ObjectDeepDetailMatcher(savedSample));
        assertThat(sampleService.findById(sample2.getId()).isPresent(), is(false));
        assertThat(sampleService.findById(sample3.getId()).isPresent(), is(false));
    }

    @Test
    public void shouldVerifyTheExistanceOfElement() throws Exception {
        sample1 = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        sampleRepository.save(sample1);

        assertThat(sampleService.exists(sample1.getId()), is(true));
        assertThat(sampleService.exists(sample2.getId()), is(false));
        assertThat(sampleService.exists(sample3.getId()), is(false));
    }

    @Test
    public void create() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void shouldVerifyTheDeletionOfElement() throws Exception {
        sampleRepository.save(sample1);

        assertThat(sampleService.findById(sample1.getId()).isPresent(), is(true));

        sampleService.delete(sample1.getId());

        assertThat(sampleService.findById(sample1.getId()).isPresent(), is(false));
    }

    @Test
    public void shouldVerifyThatOnlyStatusIsUpdatedOnUpdateStatusWhenElemendSpecifiedByIdIsFound() throws Exception {
        final Map<String, Object> updates = newHashMap();
        updates.put("id", "12");
        updates.put("name", "test");
        updates.put("active", "true");

        sample1.setActive(false);
        sampleRepository.save(sample1);

        assertThat(sampleService.findById(sample1.getId()).get().isActive(), is(false));

        final Sample actualService = sampleService.updateStatus(sample1.getId(), updates);

        assertThat(actualService.getId(), is(sample1.getId()));
        assertThat(actualService.getName(), is(sample1.getName()));
        assertThat(actualService.isActive(), is(true));
    }
}