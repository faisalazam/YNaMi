package pk.lucidxpo.ynami.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import pk.lucidxpo.ynami.persistence.model.Sample;
import pk.lucidxpo.ynami.service.SampleService;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.util.Maps.newHashMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class SampleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private SampleService sampleService;
    //
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
        List<Sample> expectedSamples = asList(
                new Sample(1L, "Daenerys Targaryen", true),
                new Sample(2L, "John Snow", false)
        );

        given(sampleService.getAll()).willReturn(expectedSamples);

        mockMvc.perform(get("/samples"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("samples", expectedSamples))
                .andReturn();

        verify(sampleService, times(1)).getAll();
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Get Sample By ID =========================================

    @Test
    public void shouldGetSampleById() throws Exception {
        Sample expectedSample = new Sample(1L, "Daenerys Targaryen", false);

        given(sampleService.findById(1L)).willReturn(of(expectedSample));

        mockMvc.perform(get("/samples/{id}", 1L))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("welcome"))
                .andExpect(model().attribute("sample", expectedSample))
                .andReturn();

        verify(sampleService, times(1)).findById(1L);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldReturn404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        given(sampleService.findById(1L)).willReturn(empty());

        mockMvc.perform(get("/samples/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(sampleService, times(1)).findById(1L);
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Create New Sample ========================================

    @Test
    public void shouldCreateNewSampleSuccessfully() throws Exception {
        Sample sample = new Sample(3L, "Arya Stark", true);

        given(sampleService.exists(sample.getId())).willReturn(false);
        given(sampleService.create(sample)).willReturn(sample);

        mockMvc.perform(post("/samples").flashAttr("sample", sample))
                .andExpect(status().isCreated())
                .andExpect(forwardedUrl("welcome"));

        verify(sampleService, times(1)).exists(sample.getId());
        verify(sampleService, times(1)).create(sample);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotCreateNewSampleWhenSampleAlreadyExistsAndReturnWith409ConflictStatus() throws Exception {
        Sample sample = new Sample(1L, "samplename exists", true);

        given(sampleService.exists(sample.getId())).willReturn(true);

        mockMvc.perform(post("/samples").flashAttr("sample", sample))
                .andExpect(status().isConflict())
                .andExpect(forwardedUrl("welcome"));

        verify(sampleService, times(1)).exists(sample.getId());
        verify(sampleService, never()).create(any(Sample.class));
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Update Existing Sample ===================================

    @Test
    public void shouldUpdateSampleSuccessfully() throws Exception {
        Sample sample = new Sample(1L, "Arya Stark", false);

        given(sampleService.findById(sample.getId())).willReturn(of(sample));
        given(sampleService.update(sample)).willReturn(sample);

        mockMvc.perform(put("/samples/{id}", sample.getId()).flashAttr("sample", sample))
                .andExpect(status().isOk());

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, times(1)).update(sample);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotUpdateAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample sample = new Sample(0L, "sample not found", false);

        given(sampleService.findById(sample.getId())).willReturn(empty());

        mockMvc.perform(
                put("/samples/{id}", sample.getId()).flashAttr("sample", sample))
                .andExpect(status().isNotFound());

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, never()).update(any(Sample.class));
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Patch Sample ============================================

    @Test
    public void shouldUpdateSamplePartiallySuccessfully() throws Exception {
        Sample sample = new Sample(1L, "Arya Stark", false);
        final Map<String, Object> updates = newHashMap("active", TRUE);

        given(sampleService.findById(sample.getId())).willReturn(of(sample));
        given(sampleService.updateStatus(sample.getId(), updates)).willReturn(sample);

        mockMvc.perform(
                patch("/samples/{id}", sample.getId())
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates))
        )
                .andExpect(status().isOk())
                .andExpect(content().string("Sample state has been updated successfully"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, times(1)).updateStatus(sample.getId(), updates);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotUpdateSamplePartiallyAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample sample = new Sample(0L, "sample not found", true);
        final Map<String, Object> updates = newHashMap("active", TRUE);

        given(sampleService.findById(sample.getId())).willReturn(empty());

        mockMvc.perform(
                patch("/samples/{id}", sample.getId())
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates))
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sample not found"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, never()).updateStatus(anyLong(), anyMap());
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Delete Sample ============================================

    @Test
    public void shouldDeleteSampleSuccessfully() throws Exception {
        Sample sample = new Sample(1L, "Arya Stark", true);

        given(sampleService.findById(sample.getId())).willReturn(of(sample));
        doNothing().when(sampleService).delete(sample.getId());

        mockMvc.perform(
                delete("/samples/{id}", sample.getId()))
                .andExpect(status().isOk());

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, times(1)).delete(sample.getId());
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotDeleteAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample sample = new Sample(0L, "sample not found", true);

        given(sampleService.findById(sample.getId())).willReturn(empty());

        mockMvc.perform(
                delete("/samples/{id}", sample.getId()))
                .andExpect(status().isNotFound());

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, never()).delete(anyLong());
        verifyNoMoreInteractions(sampleService);
    }
}