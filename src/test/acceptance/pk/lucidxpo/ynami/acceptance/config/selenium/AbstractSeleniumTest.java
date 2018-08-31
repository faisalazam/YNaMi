package pk.lucidxpo.ynami.acceptance.config.selenium;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pk.lucidxpo.ynami.spring.features.FeatureManagerWrappable;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
public abstract class AbstractSeleniumTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected FeatureManagerWrappable featureManager;
}