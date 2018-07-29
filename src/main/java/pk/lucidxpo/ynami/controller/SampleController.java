package pk.lucidxpo.ynami.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pk.lucidxpo.ynami.persistence.model.Sample;
import pk.lucidxpo.ynami.service.SampleService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
public class SampleController {

    @Value("${welcome.message}")
    private String message;

    @Autowired
    private SampleService sampleService;

    @RequestMapping("/")
    public String welcome(Model model) {
        model.addAttribute("message", message);
        return "welcome";
    }

    @GetMapping(value = "/samples")
    public String getSamples(Model model) {
        final List<Sample> samples = sampleService.getAll();

        model.addAttribute("samples", samples);

        return "listSamples";
    }

    @GetMapping(value = "/samples/new")
    public String prepareSampleCreation(Model model) {
        model.addAttribute("sample", new Sample());
        return "createSample";
    }

    @PostMapping(value = "/samples")
    public String createSample(@ModelAttribute("sample") Sample sample, HttpServletResponse response) {
        if (sampleService.exists(sample.getId())) {
            response.setStatus(CONFLICT.value());
            return "createSample";
        }
        sampleService.create(sample);
        return "redirect:/samples";
    }

    @GetMapping(value = "/samples/{id}/view")
    public String getSample(@PathVariable Long id, Model model, HttpServletResponse response) {
        final Optional<Sample> sample = sampleService.findById(id);

        if (sample.isPresent()) {
            model.addAttribute("sample", sample.get());
            response.setStatus(FOUND.value());
        } else {
            response.setStatus(NOT_FOUND.value());
        }
        return "viewSample";
    }

    @GetMapping(value = "/samples/{id}")
    public String prepareSampleUpdation(@PathVariable final Long id, final Model model, final HttpServletResponse response) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            model.addAttribute("sample", optionalSample.get());
            response.setStatus(FOUND.value());
        } else {
            response.setStatus(NOT_FOUND.value());
        }
        return "editSample";
    }

    @PutMapping(value = "/samples/{id}")
    public String updateSample(@PathVariable Long id, @ModelAttribute("sample") Sample sample, HttpServletResponse response) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            sampleService.update(sample);
            return "redirect:/samples";
        }
        response.setStatus(NOT_FOUND.value());
        return "updateSample";
    }

    @DeleteMapping(value = "/samples/{id}")
    public String deleteSample(@PathVariable Long id, HttpServletResponse response) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            sampleService.delete(id);
            return "redirect:/samples";
        }
        response.setStatus(NOT_FOUND.value());
        return "deleteSample";
    }

    @PatchMapping(value = "/samples/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSampleStatus(@PathVariable final Long id,
                                                     @RequestBody final Map<String, Object> updates) {
        final Optional<Sample> optionalSample = sampleService.findById(id);
        if (optionalSample.isPresent()) {
            sampleService.updateStatus(id, updates);
            return new ResponseEntity<>("Sample state has been updated successfully", OK);
        }
        return new ResponseEntity<>("Sample not found", NOT_FOUND);
    }
}