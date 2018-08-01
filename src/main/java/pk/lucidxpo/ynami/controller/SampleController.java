package pk.lucidxpo.ynami.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pk.lucidxpo.ynami.persistence.dto.SampleCreationDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdateStatusDTO;
import pk.lucidxpo.ynami.persistence.dto.SampleUpdationDTO;
import pk.lucidxpo.ynami.persistence.model.Sample;
import pk.lucidxpo.ynami.service.SampleService;
import pk.lucidxpo.ynami.spring.features.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static pk.lucidxpo.ynami.spring.features.AvailableFeatures.CONDITIONAL_STATEMENTS_EXECUTION;
import static pk.lucidxpo.ynami.spring.features.AvailableFeatures.METHOD_EXECUTION;

@Controller
public class SampleController {

    @Value("${welcome.message}")
    private String message;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SampleService sampleService;

    @Autowired
    private FeatureManagerWrapper featureManager;

    @RequestMapping("/")
    @FeatureAssociation(value = METHOD_EXECUTION)
    public String welcome(Model model) {
        model.addAttribute("message", message);

        if (featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)) {
            model.addAttribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled.");
        } else {
            model.addAttribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled.");
        }

        return "welcome";
    }

    @GetMapping(value = "/samples")
    public String getSamples(final Model model) {
        final List<Sample> samples = sampleService.getAll();
        final List<SampleDTO> sampleDTOs = samples.stream()
                .map(sample -> modelMapper.map(sample, SampleDTO.class))
                .collect(toList());

        model.addAttribute("samples", sampleDTOs);

        return "sample/listSamples";
    }

    @GetMapping(value = "/samples/new")
    public String prepareSampleCreation(final Model model) {
        model.addAttribute("sample", SampleCreationDTO.builder().build());
        return "sample/createSample";
    }

    @PostMapping(value = "/samples")
    public String createSample(@ModelAttribute("sample") final SampleCreationDTO sampleCreationDTO,
                               final HttpServletResponse response) {
        if (sampleService.existsByFirstName(sampleCreationDTO.getFirstName())) {
            response.setStatus(CONFLICT.value());
            return "sample/createSample";
        }
        final Sample sample = modelMapper.map(sampleCreationDTO, Sample.class);
        sampleService.create(sample);
        return "redirect:/samples";
    }

    @GetMapping(value = "/samples/{id}/view")
    public String getSample(@PathVariable final Long id, final Model model, final HttpServletResponse response) {
        final Optional<Sample> sample = sampleService.findById(id);

        if (sample.isPresent()) {
            final SampleDTO sampleDTO = modelMapper.map(sample.get(), SampleDTO.class);
            model.addAttribute("sample", sampleDTO);
            response.setStatus(FOUND.value());
        } else {
            response.setStatus(NOT_FOUND.value());
        }
        return "sample/viewSample";
    }

    @GetMapping(value = "/samples/{id}")
    public String prepareSampleUpdation(@PathVariable final Long id, final Model model, final HttpServletResponse response) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            final SampleUpdationDTO sampleUpdationDTO = modelMapper.map(optionalSample.get(), SampleUpdationDTO.class);
            model.addAttribute("sample", sampleUpdationDTO);
            response.setStatus(FOUND.value());
        } else {
            response.setStatus(NOT_FOUND.value());
        }
        return "sample/editSample";
    }

    @PutMapping(value = "/samples/{id}")
    public String updateSample(@PathVariable final Long id,
                               @ModelAttribute("sample") final SampleUpdationDTO sampleUpdationDTO,
                               final HttpServletResponse response) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            final Sample sample = optionalSample.get();
            modelMapper.map(sampleUpdationDTO, sample);
            sampleService.update(sample);
            return "redirect:/samples";
        }
        response.setStatus(NOT_FOUND.value());
        return "sample/updateSample";
    }

    @DeleteMapping(value = "/samples/{id}")
    public String deleteSample(@PathVariable final Long id,
                               final HttpServletResponse response) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            sampleService.delete(id);
            return "redirect:/samples";
        }
        response.setStatus(NOT_FOUND.value());
        return "sample/deleteSample";
    }

    @PatchMapping(value = "/samples/{id}")
    public ResponseEntity<String> updateSampleStatus(@PathVariable final Long id,
                                                     @ModelAttribute("sample") final SampleUpdateStatusDTO sampleUpdateStatusDTO) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            final Sample sample = optionalSample.get();
            modelMapper.map(sampleUpdateStatusDTO, sample);
            sampleService.update(sample);
            return new ResponseEntity<>("Sample state has been updated successfully", OK);
        }
        return new ResponseEntity<>("Sample not found", NOT_FOUND);
    }
}