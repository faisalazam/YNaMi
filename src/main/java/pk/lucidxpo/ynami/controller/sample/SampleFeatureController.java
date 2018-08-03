package pk.lucidxpo.ynami.controller.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pk.lucidxpo.ynami.spring.features.FeatureAssociation;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrapper;

import static pk.lucidxpo.ynami.spring.features.FeatureToggles.CONDITIONAL_STATEMENTS_EXECUTION;
import static pk.lucidxpo.ynami.spring.features.FeatureToggles.METHOD_EXECUTION;

@Controller
public class SampleFeatureController {
    @Autowired
    private FeatureManagerWrapper featureManager;

    @RequestMapping("/feature-test")
    @FeatureAssociation(value = METHOD_EXECUTION)
    public String welcome(Model model) {
        if (featureManager.isActive(CONDITIONAL_STATEMENTS_EXECUTION)) {
            model.addAttribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is enabled.");
        } else {
            model.addAttribute("featureStatus", CONDITIONAL_STATEMENTS_EXECUTION.name() + " is disabled.");
        }
        return "welcome";
    }
}