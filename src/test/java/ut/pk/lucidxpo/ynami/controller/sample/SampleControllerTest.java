package ut.pk.lucidxpo.ynami.controller.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import pk.lucidxpo.ynami.controller.sample.SampleController;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.sample.SampleUpdationDTO;
import pk.lucidxpo.ynami.persistence.model.sample.Sample;
import pk.lucidxpo.ynami.service.sample.SampleService;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static pk.lucidxpo.ynami.persistence.model.sample.SampleBuilder.aSample;
import static pk.lucidxpo.ynami.utils.Identity.randomID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class SampleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SampleService sampleService;

    @InjectMocks
    private SampleController sampleController;

    @BeforeEach
    void setup() {
        mockMvc = standaloneSetup(sampleController).build();
    }

    @Test
    void shouldReturnDefaultMessage() throws Exception {
        setField(sampleController, "message", "Welcome Crazy");

        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("message", "Welcome Crazy"))
                .andReturn();
    }

    // =========================================== Get All Samples ==========================================

    @Test
    void shouldGetAllSamples() throws Exception {
        final Sample sample1 = aSample().build();
        final Sample sample2 = aSample().build();
        final List<Sample> sampleList = asList(sample1, sample2);
        given(sampleService.getAll()).willReturn(sampleList);

        final SampleDTO sampleDTO1 = SampleDTO.builder().build();
        given(modelMapper.map(sample1, SampleDTO.class)).willReturn(sampleDTO1);

        final SampleDTO sampleDTO2 = SampleDTO.builder().build();
        given(modelMapper.map(sample2, SampleDTO.class)).willReturn(sampleDTO2);

        final List<SampleDTO> expectedSamples = asList(sampleDTO1, sampleDTO2);

        mockMvc.perform(get("/samples"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("sample/listSamples"))
                .andExpect(model().attribute("samples", expectedSamples))
                .andReturn();

        verify(sampleService, times(1)).getAll();
        verify(modelMapper, times(1)).map(sample1, SampleDTO.class);
        verify(modelMapper, times(1)).map(sample2, SampleDTO.class);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    // =========================================== Get Sample By ID =========================================

    @Test
    void shouldGetSampleById() throws Exception {
        final String id = randomID();
        final Sample sample = aSample().withId(id).build();

        given(sampleService.findById(id)).willReturn(of(sample));

        final SampleDTO sampleDTO = SampleDTO.builder().build();
        given(modelMapper.map(sample, SampleDTO.class)).willReturn(sampleDTO);

        mockMvc.perform(get("/samples/{id}/view", id))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("sample/viewSample"))
                .andExpect(model().attribute("sample", sampleDTO))
                .andReturn();

        verify(modelMapper, times(1)).map(sample, SampleDTO.class);
        verify(sampleService, times(1)).findById(id);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    void shouldReturn404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final String id = randomID();
        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(get("/samples/{id}/view", id))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("sample/viewSample"))
                .andExpect(model().attributeDoesNotExist("sample"));

        verify(sampleService, times(1)).findById(id);
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Prepare New Sample Creation ==============================

    @Test
    void shouldPrepareNewSampleCreation() throws Exception {
        final SampleCreationDTO expectedSample = SampleCreationDTO.builder().build();

        mockMvc.perform(get("/samples/new"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("sample/createSample"))
                .andExpect(model().attribute("sample", expectedSample))
                .andReturn();

        verifyZeroInteractions(modelMapper, sampleService);
    }

    // =========================================== Create New Sample ========================================

    @Test
    void shouldCreateNewSampleSuccessfully() throws Exception {
        final SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .firstName(randomAlphabetic(5, 50))
                .build();

        final Sample sample = aSample().build();
        given(sampleService.existsByFirstName(sampleCreationDTO.getFirstName())).willReturn(false);
        given(sampleService.create(sample)).willReturn(sample);
        given(modelMapper.map(sampleCreationDTO, Sample.class)).willReturn(sample);

        mockMvc.perform(post("/samples").flashAttr("sample", sampleCreationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).existsByFirstName(sampleCreationDTO.getFirstName());
        verify(sampleService, times(1)).create(sample);
        verify(modelMapper, times(1)).map(sampleCreationDTO, Sample.class);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    void shouldNotCreateNewSampleWhenSampleAlreadyExistsAndReturnWith409ConflictStatus() throws Exception {
        final SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .firstName(randomAlphabetic(5, 50))
                .build();

        given(sampleService.existsByFirstName(sampleCreationDTO.getFirstName())).willReturn(true);

        mockMvc.perform(post("/samples").flashAttr("sample", sampleCreationDTO))
                .andExpect(status().isConflict())
                .andExpect(forwardedUrl("sample/createSample"));

        verify(sampleService, times(1)).existsByFirstName(sampleCreationDTO.getFirstName());
        verify(sampleService, never()).create(any(Sample.class));
        verifyZeroInteractions(modelMapper);
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Prepare Existing Sample Updation =========================

    @Test
    void shouldPrepareExistingSampleUpdationSuccessfully() throws Exception {

        final String id = randomID();
        final Sample sample = aSample().withId(id).build();
        given(sampleService.findById(id)).willReturn(of(sample));

        final SampleUpdationDTO expectedSampleUpdationDTO = SampleUpdationDTO.builder().build();
        given(modelMapper.map(sample, SampleUpdationDTO.class)).willReturn(expectedSampleUpdationDTO);

        mockMvc.perform(get("/samples/{id}", id))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("sample/editSample"))
                .andExpect(model().attribute("sample", expectedSampleUpdationDTO))
                .andReturn();
        verify(sampleService, times(1)).findById(id);
        verify(modelMapper, times(1)).map(sample, SampleUpdationDTO.class);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    void shouldNotPrepareExistingSampleUpdationAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final String id = randomID();
        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(get("/samples/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("sample/editSample"))
                .andExpect(model().attributeDoesNotExist("sample"))
                .andReturn();
        verify(sampleService, times(1)).findById(id);
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Update Existing Sample ===================================

    @Test
    void shouldUpdateSampleSuccessfully() throws Exception {
        final String id = randomID();
        final Sample sample = aSample().build();
        given(sampleService.findById(id)).willReturn(of(sample));
        given(sampleService.update(sample)).willReturn(sample);

        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder().build();
        doNothing().when(modelMapper).map(sampleUpdationDTO, sample);

        mockMvc.perform(put("/samples/{id}", id).flashAttr("sample", sampleUpdationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, times(1)).update(sample);
        verify(modelMapper, times(1)).map(sampleUpdationDTO, sample);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    void shouldNotUpdateAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final String id = randomID();
        given(sampleService.findById(id)).willReturn(empty());

        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder().build();

        mockMvc.perform(put("/samples/{id}", id).flashAttr("sample", sampleUpdationDTO))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("sample/updateSample"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, never()).update(any(Sample.class));
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Patch Sample ============================================

    @Test
    void shouldUpdateSamplePartiallySuccessfully() throws Exception {
        final String id = randomID();
        final Sample sample = aSample().build();
        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder().build();

        given(sampleService.findById(id)).willReturn(of(sample));

        mockMvc.perform(patch("/samples/{id}", id).flashAttr("sample", sampleUpdateStatusDTO))
                .andExpect(status().isOk())
                .andExpect(content().string("Sample state has been updated successfully"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, times(1)).update(sample);
        verify(modelMapper, times(1)).map(sampleUpdateStatusDTO, sample);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    void shouldNotUpdateSamplePartiallyAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final String id = randomID();
        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder().build();

        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(patch("/samples/{id}", id).flashAttr("sample", sampleUpdateStatusDTO))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sample not found"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, never()).updateStatus(anyString(), anyMap());
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Delete Sample ============================================

    @Test
    void shouldDeleteSampleSuccessfully() throws Exception {
        final String id = randomID();
        Sample sample = aSample().build();

        given(sampleService.findById(id)).willReturn(of(sample));
        doNothing().when(sampleService).delete(id);

        mockMvc.perform(
                delete("/samples/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, times(1)).delete(id);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    void shouldNotDeleteAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final String id = randomID();
        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(
                delete("/samples/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("sample/deleteSample"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, never()).delete(anyString());
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}