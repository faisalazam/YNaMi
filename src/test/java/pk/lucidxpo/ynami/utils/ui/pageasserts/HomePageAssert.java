package pk.lucidxpo.ynami.utils.ui.pageasserts;

import org.assertj.core.api.AbstractAssert;
import pk.lucidxpo.ynami.utils.ui.pageobjects.HomePage;

public class HomePageAssert extends AbstractAssert<HomePageAssert, HomePage> {
    private final HomePage homePage;

    public HomePageAssert(final HomePage homePage) {
        super(homePage, HomePageAssert.class);
        this.homePage = homePage;
    }

    public void pageLoaded() {
        homePage.waitForPageToLoad(homePage.getPageLoadCondition());
    }
}