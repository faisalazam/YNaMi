package acceptance.pk.lucidxpo.ynami.pageasserts;

import acceptance.pk.lucidxpo.ynami.pageobjects.HomePage;
import org.assertj.core.api.AbstractAssert;

public class HomePageAssert extends AbstractAssert<HomePageAssert, HomePage> {

    protected HomePageAssert(final HomePage homePage) {
        super(homePage, HomePageAssert.class);
    }
}