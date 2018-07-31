package pk.lucidxpo.ynami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import pk.lucidxpo.ynami.persistence.dto.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdationDTO;
import pk.lucidxpo.ynami.persistence.model.Sample;
import pk.lucidxpo.ynami.service.SampleService;

import java.util.List;

import static java.lang.Long.valueOf;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
import static pk.lucidxpo.ynami.persistence.model.Sample.builder;
import static pk.lucidxpo.ynami.testutils.Identity.randomInt;

@RunWith(MockitoJUnitRunner.class)
public class SampleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SampleService sampleService;

    @InjectMocks
    private SampleController sampleController;

    @Before
    public void setup() {
        mockMvc = standaloneSetup(sampleController).build();
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
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
    public void shouldGetAllSamples() throws Exception {
        final Sample sample1 = builder().build();
        final Sample sample2 = builder().build();
        final List<Sample> sampleList = asList(sample1, sample2);
        given(sampleService.getAll()).willReturn(sampleList);

        final SampleDTO sampleDTO1 = SampleDTO.builder().build();
        given(modelMapper.map(sample1, SampleDTO.class)).willReturn(sampleDTO1);

        final SampleDTO sampleDTO2 = SampleDTO.builder().build();
        given(modelMapper.map(sample2, SampleDTO.class)).willReturn(sampleDTO2);

        final List<SampleDTO> expectedSamples = asList(sampleDTO1, sampleDTO2);

        mockMvc.perform(get("/samples"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("listSamples"))
                .andExpect(model().attribute("samples", expectedSamples))
                .andReturn();

        verify(sampleService, times(1)).getAll();
        verify(modelMapper, times(2)).map(sample1, SampleDTO.class);
        verify(modelMapper, times(2)).map(sample2, SampleDTO.class);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    // =========================================== Get Sample By ID =========================================

    @Test
    public void shouldGetSampleById() throws Exception {
        final Long id = valueOf(randomInt());
        final Sample sample = builder().build();

        given(sampleService.findById(id)).willReturn(of(sample));

        final SampleDTO sampleDTO = SampleDTO.builder().build();
        given(modelMapper.map(sample, SampleDTO.class)).willReturn(sampleDTO);

        mockMvc.perform(get("/samples/{id}/view", id))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("viewSample"))
                .andExpect(model().attribute("sample", sampleDTO))
                .andReturn();

        verify(modelMapper, times(1)).map(sample, SampleDTO.class);
        verify(sampleService, times(1)).findById(id);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    public void shouldReturn404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final long id = valueOf(randomInt());
        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(get("/samples/{id}/view", id))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("viewSample"))
                .andExpect(model().attributeDoesNotExist("sample"));

        verify(sampleService, times(1)).findById(id);
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Prepare New Sample Creation ==============================

    @Test
    public void shouldPrepareNewSampleCreation() throws Exception {
        final SampleCreationDTO expectedSample = SampleCreationDTO.builder().build();

        mockMvc.perform(get("/samples/new"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("createSample"))
                .andExpect(model().attribute("sample", expectedSample))
                .andReturn();

        verifyZeroInteractions(modelMapper, sampleService);
    }

    // =========================================== Create New Sample ========================================

    @Test
    public void shouldCreateNewSampleSuccessfully() throws Exception {
        final SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .firstName(randomAlphabetic(5, 50))
                .build();

        final Sample sample = builder().build();
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
    public void shouldNotCreateNewSampleWhenSampleAlreadyExistsAndReturnWith409ConflictStatus() throws Exception {
        final SampleCreationDTO sampleCreationDTO = SampleCreationDTO.builder()
                .firstName(randomAlphabetic(5, 50))
                .build();

        given(sampleService.existsByFirstName(sampleCreationDTO.getFirstName())).willReturn(true);

        mockMvc.perform(post("/samples").flashAttr("sample", sampleCreationDTO))
                .andExpect(status().isConflict())
                .andExpect(forwardedUrl("createSample"));

        verify(sampleService, times(1)).existsByFirstName(sampleCreationDTO.getFirstName());
        verify(sampleService, never()).create(any(Sample.class));
        verifyZeroInteractions(modelMapper);
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Prepare Existing Sample Updation =========================

    @Test
    public void shouldPrepareExistingSampleUpdationSuccessfully() throws Exception {

        final Long id = valueOf(randomInt());
        final Sample sample = Sample.builder().id(id).build();
        given(sampleService.findById(id)).willReturn(of(sample));

        final SampleUpdationDTO expectedSampleUpdationDTO = SampleUpdationDTO.builder().build();
        given(modelMapper.map(sample, SampleUpdationDTO.class)).willReturn(expectedSampleUpdationDTO);

        mockMvc.perform(get("/samples/{id}", id))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("editSample"))
                .andExpect(model().attribute("sample", expectedSampleUpdationDTO))
                .andReturn();
        verify(sampleService, times(1)).findById(id);
        verify(modelMapper, times(1)).map(sample, SampleUpdationDTO.class);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    public void shouldNotPrepareExistingSampleUpdationAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final Long id = valueOf(randomInt());
        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(get("/samples/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("editSample"))
                .andExpect(model().attributeDoesNotExist("sample"))
                .andReturn();
        verify(sampleService, times(1)).findById(id);
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Update Existing Sample ===================================

    @Test
    public void shouldUpdateSampleSuccessfully() throws Exception {
        final Long id = valueOf(randomInt());
        final Sample sample = builder().build();
        given(sampleService.findById(id)).willReturn(of(sample));
        given(sampleService.update(sample)).willReturn(sample);

        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder().build();
        given(modelMapper.map(sampleUpdationDTO, Sample.class)).willReturn(sample);

        mockMvc.perform(put("/samples/{id}", id).flashAttr("sample", sampleUpdationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, times(1)).update(sample);
        verify(modelMapper, times(1)).map(sampleUpdationDTO, Sample.class);
        verifyNoMoreInteractions(sampleService, modelMapper);
    }

    @Test
    public void shouldNotUpdateAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final Long id = valueOf(randomInt());
        given(sampleService.findById(id)).willReturn(empty());

        final SampleUpdationDTO sampleUpdationDTO = SampleUpdationDTO.builder().build();

        mockMvc.perform(put("/samples/{id}", id).flashAttr("sample", sampleUpdationDTO))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("updateSample"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, never()).update(any(Sample.class));
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Patch Sample ============================================

    @Test
    public void shouldUpdateSamplePartiallySuccessfully() throws Exception {
        final Long id = valueOf(randomInt());
        final Sample sample = builder().build();
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
    public void shouldNotUpdateSamplePartiallyAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final Long id = valueOf(randomInt());
        final SampleUpdateStatusDTO sampleUpdateStatusDTO = SampleUpdateStatusDTO.builder().build();

        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(patch("/samples/{id}", id).flashAttr("sample", sampleUpdateStatusDTO))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sample not found"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, never()).updateStatus(anyLong(), anyMap());
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    // =========================================== Delete Sample ============================================

    @Test
    public void shouldDeleteSampleSuccessfully() throws Exception {
        final Long id = valueOf(randomInt());
        Sample sample = builder().build();

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
    public void shouldNotDeleteAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        final Long id = valueOf(randomInt());
        given(sampleService.findById(id)).willReturn(empty());

        mockMvc.perform(
                delete("/samples/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("deleteSample"));

        verify(sampleService, times(1)).findById(id);
        verify(sampleService, never()).delete(anyLong());
        verifyNoMoreInteractions(sampleService);
        verifyZeroInteractions(modelMapper);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}