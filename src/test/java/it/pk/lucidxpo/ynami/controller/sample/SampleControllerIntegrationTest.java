package it.pk.lucidxpo.ynami.controller.sample;

import it.pk.lucidxpo.ynami.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.Sql;
import pk.lucidxpo.ynami.persistence.dao.sample.SampleRepository;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleUpdationDTO;
import pk.lucidxpo.ynami.persistence.model.sample.Sample;
import pk.lucidxpo.ynami.utils.executionlisteners.DatabaseExecutionListener;

import java.util.List;

import static it.pk.lucidxpo.ynami.AbstractIntegrationTest.ADMIN_USER;
import static java.lang.Long.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.data.domain.Example.of;
import static org.springframework.data.domain.ExampleMatcher.matching;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static pk.lucidxpo.ynami.persistence.dto.sample.SampleCreationDTO.builder;
import static pk.lucidxpo.ynami.persistence.model.sample.SampleBuilder.aSample;
import static pk.lucidxpo.ynami.utils.Identity.randomID;
import static pk.lucidxpo.ynami.utils.Identity.randomInt;
import static pk.lucidxpo.ynami.utils.Randomly.chooseOneOf;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@WithUserDetails(value = ADMIN_USER)
@Sql(executionPhase = BEFORE_TEST_METHOD,
        scripts = {
                "classpath:insert-roles.sql",
                "classpath:insert-users.sql"
        }
)
@TestExecutionListeners(value = DatabaseExecutionListener.class, mergeMode = MERGE_WITH_DEFAULTS)
class SampleControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SampleRepository sampleRepository;

    @BeforeEach
    void setup() {
        sampleRepository.deleteAll();
    }

    // =========================================== Get All Samples ==========================================

    @Test
    void shouldGetAllSamples() throws Exception {
        final Sample sample1 = sampleRepository.saveAndFlush(aSample().build());
        final Sample sample2 = sampleRepository.saveAndFlush(aSample().build());

        final List<SampleDTO> expectedSamples = asList(
                modelMapper.map(sample1, SampleDTO.class),
                modelMapper.map(sample2, SampleDTO.class)
        );

        mockMvc.perform(get("/samples"))
                .andExpect(status().isOk())
                .andExpect(view().name("sample/listSamples"))
                .andExpect(model().attribute("samples", containsInAnyOrder(expectedSamples.toArray())))
                .andReturn();
    }

    // =========================================== Get Sample By ID =========================================

    @Test
    void shouldGetSampleById() throws Exception {
        final Sample sample = sampleRepository.saveAndFlush(aSample().build());
        final SampleDTO sampleDTO = modelMapper.map(sample, SampleDTO.class);

        mockMvc.perform(get("/samples/{id}/view", sample.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("sample/viewSample"))
                .andExpect(model().attribute("sample", sampleDTO))
                .andReturn();
    }

    @Test
    void shouldReturn404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        mockMvc.perform(get("/samples/{id}/view", valueOf(randomInt())))
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/viewSample"))
                .andExpect(model().attributeDoesNotExist("sample"));
    }

    // =========================================== Prepare New Sample Creation ==============================

    @Test
    void shouldPrepareNewSampleCreation() throws Exception {
        final SampleCreationDTO expectedSample = builder().build();

        mockMvc.perform(get("/samples/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("sample/createSample"))
                .andExpect(model().attribute("sample", expectedSample))
                .andReturn();
    }

    // =========================================== Create New Sample ========================================

    @Test
    void shouldCreateNewSampleSuccessfully() throws Exception {
        final SampleCreationDTO sampleCreationDTO = builder()
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .address(randomAlphabetic(5, 50))
                .active(chooseOneOf(true, false))
                .build();

        mockMvc.perform(
                        post("/samples")
                                .with(csrf())
                                .flashAttr("sample", sampleCreationDTO)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"))
                .andReturn();
        final Sample createdSample = sampleRepository.findOne(of(modelMapper.map(sampleCreationDTO, Sample.class), matching().withIgnorePaths("id").withIgnoreNullValues())).get();
        assertAll(
                () -> assertEquals(sampleCreationDTO.isActive(), createdSample.isActive()),
                () -> assertEquals(sampleCreationDTO.getAddress(), createdSample.getAddress()),
                () -> assertEquals(sampleCreationDTO.getFirstName(), createdSample.getFirstName()),
                () -> assertEquals(sampleCreationDTO.getLastName(), createdSample.getLastName())
        );
    }

    @Test
    void shouldNotCreateNewSampleWhenSampleAlreadyExistsAndReturnWith409ConflictStatus() throws Exception {
        final String firstName = randomAlphabetic(5, 50);
        final Sample sample = aSample()
                .withFirstName(firstName)
                .build();
        sampleRepository.saveAndFlush(sample);

        final SampleCreationDTO sampleCreationDTO = builder()
                .firstName(firstName)
                .build();

        mockMvc.perform(
                        post("/samples")
                                .with(csrf())
                                .flashAttr("sample", sampleCreationDTO)
                )
                .andExpect(status().isConflict())
                .andExpect(view().name("sample/createSample"));
    }

    // =========================================== Prepare Existing Sample Updation =========================

    @Test
    void shouldPrepareExistingSampleUpdationSuccessfully() throws Exception {
        final Sample sample = sampleRepository.saveAndFlush(aSample().build());
        final SampleUpdationDTO expectedSampleUpdationDTO = modelMapper.map(sample, SampleUpdationDTO.class);

        mockMvc.perform(get("/samples/{id}", sample.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("sample/editSample"))
                .andExpect(model().attribute("sample", expectedSampleUpdationDTO))
                .andReturn();
    }

    @Test
    void shouldNotPrepareExistingSampleUpdationAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        mockMvc.perform(get("/samples/{id}", valueOf(randomInt())))
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/editSample"))
                .andExpect(model().attributeDoesNotExist("sample"))
                .andReturn();
    }

    // =========================================== Update Existing Sample ===================================

    @Test
    void shouldUpdateSampleSuccessfully() throws Exception {
        final Sample sample = aSample()
                .withActive(false)
                .build();
        final Sample savedSample = sampleRepository.saveAndFlush(sample);

        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder()
                .active(true)
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();

        mockMvc.perform(
                        put("/samples/{id}", savedSample.getId())
                                .with(csrf())
                                .flashAttr("sample", sampleUpdationDTO)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        final Sample updatedSample = sampleRepository.findById(savedSample.getId()).get();
        assertAll(
                () -> assertEquals(sampleUpdationDTO.isActive(), updatedSample.isActive()),
                () -> assertEquals(savedSample.getAddress(), updatedSample.getAddress()),
                () -> assertEquals(sampleUpdationDTO.getFirstName(), updatedSample.getFirstName()),
                () -> assertEquals(sampleUpdationDTO.getLastName(), updatedSample.getLastName())
        );
    }

    @Test
    void shouldNotUpdateAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder().build();

        mockMvc.perform(
                        put("/samples/{id}", valueOf(randomInt()))
                                .with(csrf())
                                .flashAttr("sample", sampleUpdationDTO)
                )
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/updateSample"));
    }

    // =========================================== Patch Sample ============================================

    @Test
    void shouldUpdateSamplePartiallySuccessfully() throws Exception {
        Sample sample = aSample()
                .withActive(true)
                .build();
        final Sample savedSample = sampleRepository.saveAndFlush(sample);

        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder()
                .active(false)
                .build();

        mockMvc.perform(
                        patch("/samples/{id}", savedSample.getId())
                                .with(csrf())
                                .flashAttr("sample", sampleUpdateStatusDTO)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Sample state has been updated successfully"));

        final Sample updatedSample = sampleRepository.findById(savedSample.getId()).get();
        assertAll(
                () -> assertFalse(updatedSample.isActive()),
                () -> assertEquals(savedSample.getAddress(), updatedSample.getAddress()),
                () -> assertEquals(savedSample.getFirstName(), updatedSample.getFirstName()),
                () -> assertEquals(savedSample.getLastName(), updatedSample.getLastName())
        );
    }

    @Test
    void shouldNotUpdateSamplePartiallyAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final String id = randomID();
        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder().build();

        mockMvc.perform(
                        patch("/samples/{id}", id)
                                .with(csrf())
                                .flashAttr("sample", sampleUpdateStatusDTO)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sample not found"));
    }

    // =========================================== Delete Sample ============================================

    @Test
    void shouldDeleteSampleSuccessfully() throws Exception {
        final Sample sample = sampleRepository.saveAndFlush(aSample().build());

        mockMvc.perform(
                        delete("/samples/{id}", sample.getId())
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));
    }

    @Test
    void shouldNotDeleteAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        mockMvc.perform(
                        delete("/samples/{id}", valueOf(randomInt()))
                                .with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/deleteSample"));
    }
}