package pk.lucidxpo.ynami.controller;

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import pk.lucidxpo.ynami.AbstractIntegrationTest;
import pk.lucidxpo.ynami.persistence.dao.SampleRepository;
import pk.lucidxpo.ynami.persistence.dto.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdationDTO;
import pk.lucidxpo.ynami.persistence.model.Sample;

import java.util.List;

import static java.lang.Long.valueOf;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.springframework.data.domain.Example.of;
import static org.springframework.data.domain.ExampleMatcher.matching;
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
import static pk.lucidxpo.ynami.persistence.model.Sample.builder;
import static pk.lucidxpo.ynami.testutils.Identity.randomInt;
import static pk.lucidxpo.ynami.testutils.Randomly.chooseOneOf;

public class SampleControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SampleRepository sampleRepository;

    @Before
    public void setup() {
        sampleRepository.deleteAll();
    }

    // =========================================== Get All Samples ==========================================

    @Test
    public void shouldGetAllSamples() throws Exception {
        Sample sample1 = builder()
                .id(valueOf(randomInt()))
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        Sample sample2 = builder()
                .id(valueOf(randomInt()))
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample1 = sampleRepository.saveAndFlush(sample1);
        sample2 = sampleRepository.saveAndFlush(sample2);

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
    public void shouldGetSampleById() throws Exception {
        Sample sample = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample = sampleRepository.saveAndFlush(sample);

        final SampleDTO sampleDTO = modelMapper.map(sample, SampleDTO.class);

        mockMvc.perform(get("/samples/{id}/view", sample.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("sample/viewSample"))
                .andExpect(model().attribute("sample", sampleDTO))
                .andReturn();
    }

    @Test
    public void shouldReturn404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        mockMvc.perform(get("/samples/{id}/view", valueOf(randomInt())))
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/viewSample"))
                .andExpect(model().attributeDoesNotExist("sample"));
    }

    // =========================================== Prepare New Sample Creation ==============================

    @Test
    public void shouldPrepareNewSampleCreation() throws Exception {
        final SampleCreationDTO expectedSample = SampleCreationDTO.builder().build();

        mockMvc.perform(get("/samples/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("sample/createSample"))
                .andExpect(model().attribute("sample", expectedSample))
                .andReturn();
    }

    // =========================================== Create New Sample ========================================

    @Test
    public void shouldCreateNewSampleSuccessfully() throws Exception {
        final SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .address(randomAlphabetic(5, 50))
                .active(chooseOneOf(true, false))
                .build();

        mockMvc.perform(post("/samples").flashAttr("sample", sampleCreationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"))
                .andReturn();
        final Sample createdSample = sampleRepository.findOne(of(modelMapper.map(sampleCreationDTO, Sample.class), matching().withIgnoreNullValues())).get();
        assertThat(createdSample.isActive(), is(sampleCreationDTO.isActive()));
        assertThat(createdSample.getAddress(), is(sampleCreationDTO.getAddress()));
        assertThat(createdSample.getFirstName(), is(sampleCreationDTO.getFirstName()));
        assertThat(createdSample.getLastName(), is(sampleCreationDTO.getLastName()));
    }

    @Test
    public void shouldNotCreateNewSampleWhenSampleAlreadyExistsAndReturnWith409ConflictStatus() throws Exception {
        final String firstName = randomAlphabetic(5, 50);
        Sample sample = builder()
                .firstName(firstName)
                .id(valueOf(randomInt()))
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sampleRepository.saveAndFlush(sample);

        final SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .firstName(firstName)
                .build();

        mockMvc.perform(post("/samples").flashAttr("sample", sampleCreationDTO))
                .andExpect(status().isConflict())
                .andExpect(view().name("sample/createSample"));
    }

    // =========================================== Prepare Existing Sample Updation =========================

    @Test
    public void shouldPrepareExistingSampleUpdationSuccessfully() throws Exception {
        Sample sample = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample = sampleRepository.saveAndFlush(sample);

        final SampleUpdationDTO expectedSampleUpdationDTO = modelMapper.map(sample, SampleUpdationDTO.class);

        mockMvc.perform(get("/samples/{id}", sample.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("sample/editSample"))
                .andExpect(model().attribute("sample", expectedSampleUpdationDTO))
                .andReturn();
    }

    @Test
    public void shouldNotPrepareExistingSampleUpdationAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        mockMvc.perform(get("/samples/{id}", valueOf(randomInt())))
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/editSample"))
                .andExpect(model().attributeDoesNotExist("sample"))
                .andReturn();
    }

    // =========================================== Update Existing Sample ===================================

    @Test
    public void shouldUpdateSampleSuccessfully() throws Exception {
        Sample sample = builder()
                .active(false)
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample = sampleRepository.saveAndFlush(sample);

        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder()
                .active(true)
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();

        mockMvc.perform(put("/samples/{id}", sample.getId()).flashAttr("sample", sampleUpdationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        final Sample updatedSample = sampleRepository.findById(sample.getId()).get();
        assertThat(updatedSample.isActive(), is(sampleUpdationDTO.isActive()));
        assertThat(updatedSample.getAddress(), is(sample.getAddress()));
        assertThat(updatedSample.getFirstName(), is(sampleUpdationDTO.getFirstName()));
        assertThat(updatedSample.getLastName(), is(sampleUpdationDTO.getLastName()));
    }

    @Test
    public void shouldNotUpdateAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder().build();

        mockMvc.perform(
                put("/samples/{id}", valueOf(randomInt()))
                        .flashAttr("sample", sampleUpdationDTO)
        )
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/updateSample"));
    }

    // =========================================== Patch Sample ============================================

    @Test
    public void shouldUpdateSamplePartiallySuccessfully() throws Exception {
        Sample sample = builder()
                .active(true)
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample = sampleRepository.saveAndFlush(sample);

        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder()
                .active(false)
                .build();

        mockMvc.perform(patch("/samples/{id}", sample.getId()).flashAttr("sample", sampleUpdateStatusDTO))
                .andExpect(status().isOk())
                .andExpect(content().string("Sample state has been updated successfully"));

        final Sample updatedSample = sampleRepository.findById(sample.getId()).get();
        assertThat(updatedSample.isActive(), is(false));
        assertThat(updatedSample.getAddress(), is(sample.getAddress()));
        assertThat(updatedSample.getFirstName(), is(sample.getFirstName()));
        assertThat(updatedSample.getLastName(), is(sample.getLastName()));
    }

    @Test
    public void shouldNotUpdateSamplePartiallyAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final Long id = valueOf(randomInt());
        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder().build();

        mockMvc.perform(patch("/samples/{id}", id).flashAttr("sample", sampleUpdateStatusDTO))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sample not found"));
    }

    // =========================================== Delete Sample ============================================

    @Test
    public void shouldDeleteSampleSuccessfully() throws Exception {
        Sample sample = builder()
                .active(chooseOneOf(true, false))
                .address(randomAlphabetic(5, 50))
                .firstName(randomAlphabetic(5, 50))
                .lastName(randomAlphabetic(5, 50))
                .build();
        sample = sampleRepository.saveAndFlush(sample);

        mockMvc.perform(
                delete("/samples/{id}", sample.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));
    }

    @Test
    public void shouldNotDeleteAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        mockMvc.perform(
                delete("/samples/{id}", valueOf(randomInt())))
                .andExpect(status().isNotFound())
                .andExpect(view().name("sample/deleteSample"));
    }
}