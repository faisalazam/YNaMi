package pk.lucidxpo.ynami.common;

import org.joda.time.LocalDateTime;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;

public class TimeFreezeExecutionListener implements TestExecutionListener {
    private static final long FROZEN_TIME = new LocalDateTime().toDateTime().getMillis();

    @Override
    public void beforeTestMethod(final TestContext testContext) {
        freezeTime();
    }

    @Override
    public void afterTestMethod(final TestContext testContext) {
        unFreezeTime();
    }

    private void freezeTime() {
        setCurrentMillisFixed(FROZEN_TIME);
    }

    private void unFreezeTime() {
        setCurrentMillisSystem();
    }
}