package pk.lucidxpo.ynami.controller.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pk.lucidxpo.ynami.service.sample.ToggleableService;
import pk.lucidxpo.ynami.spring.aspect.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.CONDITIONAL_STATEMENTS_EXECUTION;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.METHOD_EXECUTION;

@Controller
public class SampleFeatureController {
    private final ToggleableService toggleableService;
    private final FeatureManagerWrappable featureManager;

    @Autowired
    public SampleFeatureController(final ToggleableService toggleableService,
                                   final FeatureManagerWrappable featureManager) {
        this.featureManager = featureManager;
        this.toggleableService = toggleableService;
    }

    @RequestMapping("/feature-test")
    @FeatureAssociation(value = METHOD_EXECUTION)
    public String welcome(Model model) {
        if (featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)) {
            model.addAttribute("message", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled.");
        } else {
            model.addAttribute("message", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled.");
        }
        return "welcome";
    }

    @ResponseBody
    @GetMapping(value = "/some-service")
    public String getToggleableService() {
        return toggleableService.getSomeValue();
    }
}