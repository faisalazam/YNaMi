package pk.lucidxpo.ynami.spring.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;

public enum AvailableFeatures implements Feature {
    @Label("Controller Execution Feature")
    CONTROLLER_EXECUTION,

    @Label("Method Execution Feature")
    METHOD_EXECUTION,

    @Label("Conditional Statements Execution Feature")
    CONDITIONAL_STATEMENTS_EXECUTION;
}