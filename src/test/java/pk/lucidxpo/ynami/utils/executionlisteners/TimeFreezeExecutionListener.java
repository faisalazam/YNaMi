package pk.lucidxpo.ynami.utils.executionlisteners;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.joda.time.Instant.now;

public class TimeFreezeExecutionListener implements TestExecutionListener {
    private static final long FROZEN_TIME = now().getMillis();

    @Override
    public void beforeTestMethod(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        freezeTime();
    }

    @Override
    public void afterTestMethod(@SuppressWarnings("NullableProblems") final TestContext testContext) {
        unFreezeTime();
    }

    private void freezeTime() {
        setCurrentMillisFixed(FROZEN_TIME);
    }

    private void unFreezeTime() {
        setCurrentMillisSystem();
    }
}