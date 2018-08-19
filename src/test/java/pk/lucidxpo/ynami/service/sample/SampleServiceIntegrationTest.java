package pk.lucidxpo.ynami.service.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.dao.sample.SampleRepository;
import pk.lucidxpo.ynami.persistence.model.sample.Sample;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;
import pk.lucidxpo.ynami.utils.executionlisteners.TimeFreezeExecutionListener;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static pk.lucidxpo.ynami.persistence.model.sample.SampleBuilder.aSample;
import static pk.lucidxpo.ynami.utils.Identity.randomID;
import static pk.lucidxpo.ynami.utils.matchers.ObjectDeepDetailMatcher.equivalentTo;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(mergeMode = MERGE_WITH_DEFAULTS,
        value = {
                DatabaseExecutionListener.class,
                TimeFreezeExecutionListener.class
        }
)
class SampleServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private SampleService sampleService;

    @Autowired
    private SampleRepository sampleRepository;

    private Sample sample1;
    private Sample sample2;
    private Sample sample3;
    private Sample sample4;
    private Sample sample5;

    @BeforeEach
    void setup() {
        sampleRepository.deleteAll();

        sample1 = aSample().build();
        sample2 = aSample().build();
        sample3 = aSample().build();
        sample4 = aSample().build();
        sample5 = aSample().build();
    }

    @Test
    @WithUserDetails(value = ADMIN_USER)
    void shouldVerifyThatAllElementsAreReturnedOnGetAll() {
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
    void shouldVerifyThatNoElementIsReturnedOnGetAllWhenThereIsNoElement() {
        final List<Sample> actualSamples = sampleService.getAll();

        assertThat(actualSamples.isEmpty(), is(true));
    }

    @Test
    @WithUserDetails(value = ADMIN_USER)
    void shouldVerifyTheRetrievalOfElementById() {
        sample1 = aSample().build();
        final Sample savedSample = sampleRepository.save(sample1);

        assertThat(sampleService.findById(savedSample.getId()).get(), equivalentTo(savedSample));
        assertThat(sampleService.findById(randomID()).isPresent(), is(false));
    }

    @Test
    void shouldVerifyTheExistenceOfElement() {
        sample1 = aSample().build();
        sampleRepository.save(sample1);

        assertThat(sampleService.existsByFirstName(sample1.getFirstName()), is(true));
        assertThat(sampleService.existsByFirstName(sample2.getFirstName()), is(false));
        assertThat(sampleService.existsByFirstName(sample3.getFirstName()), is(false));
    }

    @Test
    void shouldVerifyThatSampleIsCreatedSuccessfullyOnCreate() {
        sample1 = aSample().build();
        final Sample createdSample = sampleService.create(sample1);

        final Sample actualSample = sampleService.findById(createdSample.getId()).get();
        assertAll(
                () -> assertEquals(sample1.isActive(), actualSample.isActive()),
                () -> assertEquals(sample1.getAddress(), actualSample.getAddress()),
                () -> assertEquals(sample1.getFirstName(), actualSample.getFirstName()),
                () -> assertEquals(sample1.getLastName(), actualSample.getLastName())
        );
    }

    @Test
    void shouldVerifyThatSampleIsUpdatedSuccessfullyOnUpdate() {
        sample1 = aSample()
                .withActive(true)
                .build();
        final Sample createdSample = sampleService.create(sample1);

        final Sample actualCreatedSample = sampleService.findById(createdSample.getId()).get();
        assertAll(
                () -> assertEquals(createdSample.isActive(), actualCreatedSample.isActive()),
                () -> assertEquals(createdSample.getAddress(), actualCreatedSample.getAddress()),
                () -> assertEquals(createdSample.getFirstName(), actualCreatedSample.getFirstName()),
                () -> assertEquals(createdSample.getLastName(), actualCreatedSample.getLastName())
        );

        createdSample.setActive(false);
        createdSample.setAddress("Dummy Address");
        createdSample.setFirstName("First Name");
        createdSample.setLastName("Last Name");
        sampleService.update(createdSample);

        final Sample actualUpdatedSample = sampleService.findById(createdSample.getId()).get();
        assertAll(
                () -> assertFalse(actualUpdatedSample.isActive()),
                () -> assertEquals("Dummy Address", actualUpdatedSample.getAddress()),
                () -> assertEquals("First Name", actualUpdatedSample.getFirstName()),
                () -> assertEquals("Last Name", actualUpdatedSample.getLastName())
        );
    }

    @Test
    void shouldVerifyTheDeletionOfElement() {
        sample1 = sampleRepository.save(sample1);

        assertThat(sampleService.findById(sample1.getId()).isPresent(), is(true));

        sampleService.delete(sample1.getId());

        assertThat(sampleService.findById(sample1.getId()).isPresent(), is(false));
    }

    @Test
    void shouldVerifyThatOnlyStatusIsUpdatedOnUpdateStatusWhenElementSpecifiedByIdIsFound() {
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

        assertAll(
                () -> assertTrue(actualService.isActive()),
                () -> assertEquals(savedSample.getId(), actualService.getId()),
                () -> assertEquals(sample1.getFirstName(), actualService.getFirstName()),
                () -> assertEquals(sample1.getLastName(), actualService.getLastName()),
                () -> assertEquals(sample1.getAddress(), actualService.getAddress()),

                () -> assertTrue(actualService.getLastModifiedDate().isAfter(savedSample.getLastModifiedDate()))
        );
    }
}