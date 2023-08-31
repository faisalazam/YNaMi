package pk.lucidxpo.ynami.utils.ui.pageasserts;

import pk.lucidxpo.ynami.utils.ui.pageobjects.HomePage;
import org.assertj.core.api.AbstractAssert;

public class HomePageAssert extends AbstractAssert<HomePageAssert, HomePage> {

    protected HomePageAssert(final HomePage homePage) {
        super(homePage, HomePageAssert.class);
    }
}