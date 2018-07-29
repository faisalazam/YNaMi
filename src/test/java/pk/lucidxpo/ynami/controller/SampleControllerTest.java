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
import pk.lucidxpo.ynami.testutils.ObjectDeepDetailMatcher;

import java.util.List;
import java.util.Map;

import static java.lang.Long.valueOf;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static pk.lucidxpo.ynami.testutils.Identity.randomInt;
import static pk.lucidxpo.ynami.testutils.Randomly.chooseOneOf;

@RunWith(MockitoJUnitRunner.class)
public class SampleControllerTest {
    private MockMvc mockMvc;

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
        List<Sample> expectedSamples = asList(
                new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false)),
                new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false))
        );

        given(sampleService.getAll()).willReturn(expectedSamples);

        mockMvc.perform(get("/samples"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("listSamples"))
                .andExpect(model().attribute("samples", expectedSamples))
                .andReturn();

        verify(sampleService, times(1)).getAll();
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Get Sample By ID =========================================

    @Test
    public void shouldGetSampleById() throws Exception {
        Sample expectedSample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.findById(expectedSample.getId())).willReturn(of(expectedSample));

        mockMvc.perform(get("/samples/{id}/view", expectedSample.getId()))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("viewSample"))
                .andExpect(model().attribute("sample", expectedSample))
                .andReturn();

        verify(sampleService, times(1)).findById(expectedSample.getId());
        verifyNoMoreInteractions(sampleService);
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
    }

    // =========================================== Prepare New Sample Creation ==============================

    @Test
    public void shouldPrepareNewSampleCreation() throws Exception {
        Sample expectedSample = new Sample();

        mockMvc.perform(get("/samples/new"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("createSample"))
                .andExpect(model().attribute("sample", new ObjectDeepDetailMatcher(expectedSample)))
                .andReturn();
    }

    // =========================================== Create New Sample ========================================

    @Test
    public void shouldCreateNewSampleSuccessfully() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.exists(sample.getId())).willReturn(false);
        given(sampleService.create(sample)).willReturn(sample);

        mockMvc.perform(post("/samples").flashAttr("sample", sample))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).exists(sample.getId());
        verify(sampleService, times(1)).create(sample);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotCreateNewSampleWhenSampleAlreadyExistsAndReturnWith409ConflictStatus() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.exists(sample.getId())).willReturn(true);

        mockMvc.perform(post("/samples").flashAttr("sample", sample))
                .andExpect(status().isConflict())
                .andExpect(forwardedUrl("createSample"));

        verify(sampleService, times(1)).exists(sample.getId());
        verify(sampleService, never()).create(any(Sample.class));
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Prepare Existing Sample Updation =========================

    @Test
    public void shouldPrepareExistingSampleUpdationSuccessfully() throws Exception {
        Sample expectedSample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        given(sampleService.findById(expectedSample.getId())).willReturn(of(expectedSample));

        mockMvc.perform(get("/samples/{id}", expectedSample.getId()))
                .andExpect(status().isFound())
                .andExpect(forwardedUrl("editSample"))
                .andExpect(model().attribute("sample", new ObjectDeepDetailMatcher(expectedSample)))
                .andReturn();
        verify(sampleService, times(1)).findById(expectedSample.getId());
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotPrepareExistingSampleUpdationAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample expectedSample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        given(sampleService.findById(expectedSample.getId())).willReturn(empty());

        mockMvc.perform(get("/samples/{id}", expectedSample.getId()))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("editSample"))
                .andExpect(model().attributeDoesNotExist("sample"))
                .andReturn();
        verify(sampleService, times(1)).findById(expectedSample.getId());
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Update Existing Sample ===================================

    @Test
    public void shouldUpdateSampleSuccessfully() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.findById(sample.getId())).willReturn(of(sample));
        given(sampleService.update(sample)).willReturn(sample);

        mockMvc.perform(put("/samples/{id}", sample.getId()).flashAttr("sample", sample))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, times(1)).update(sample);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotUpdateAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.findById(sample.getId())).willReturn(empty());

        mockMvc.perform(
                put("/samples/{id}", sample.getId()).flashAttr("sample", sample))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("updateSample"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, never()).update(any(Sample.class));
        verifyNoMoreInteractions(sampleService);
    }

    // =========================================== Patch Sample ============================================

    @Test
    public void shouldUpdateSamplePartiallySuccessfully() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        final Map<String, Object> updates = newHashMap("active", chooseOneOf(true, false));

        given(sampleService.findById(sample.getId())).willReturn(of(sample));
        given(sampleService.updateStatus(sample.getId(), updates)).willReturn(sample);

        mockMvc.perform(
                patch("/samples/{id}", sample.getId())
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(updates))
        )
                .andExpect(status().isOk())
                .andExpect(content().string("Sample state has been updated successfully"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, times(1)).updateStatus(sample.getId(), updates);
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotUpdateSamplePartiallyAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));
        final Map<String, Object> updates = newHashMap("active", chooseOneOf(true, false));

        given(sampleService.findById(sample.getId())).willReturn(empty());

        mockMvc.perform(
                patch("/samples/{id}", sample.getId())
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(updates))
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
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.findById(sample.getId())).willReturn(of(sample));
        doNothing().when(sampleService).delete(sample.getId());

        mockMvc.perform(
                delete("/samples/{id}", sample.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/samples"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, times(1)).delete(sample.getId());
        verifyNoMoreInteractions(sampleService);
    }

    @Test
    public void shouldNotDeleteAndReturnWith404NotFoundWhenSampleWithProvidedIdIsNotFound() throws Exception {
        Sample sample = new Sample(valueOf(randomInt()), randomAlphabetic(5, 50), chooseOneOf(true, false));

        given(sampleService.findById(sample.getId())).willReturn(empty());

        mockMvc.perform(
                delete("/samples/{id}", sample.getId()))
                .andExpect(status().isNotFound())
                .andExpect(forwardedUrl("deleteSample"));

        verify(sampleService, times(1)).findById(sample.getId());
        verify(sampleService, never()).delete(anyLong());
        verifyNoMoreInteractions(sampleService);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}