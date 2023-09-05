

If running bdd tests from IntelliJ fails, remember to do clean and rebuild of the project before retrying.

```
public WebDriver chromeDriver() {
        final WebDriverManager webDriverManager = chromedriver();
        final ChromeOptions options = new ChromeOptions();
        options.addArguments(TEST_TYPE);
        options.addArguments(NO_SANDBOX);
        options.setAcceptInsecureCerts(true);
        options.addArguments(DISABLE_DEV_SHM_USAGE);

        // default setting will be headless mode
        final String headlessMode = getenv(HEADLESS_MODE);
        if (headlessMode == null || TRUE.equals(valueOf(headlessMode))) {
            options.addArguments(HEADLESS);
            options.addArguments(
                    "--headless=new",
                    "--start-maximized",
                    "--ignore-ssl-errors",
                    "--window-position=0,0",
                    "--window-size=1920,1200",
                    "--ignore-certificate-errors",
                    "--allow-insecure-localhost",
                    "--allow-running-insecure-content",
                    "--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36",
                    "--silent"
            );
            options.addArguments("--remote-debugging-port=9222");
//            options.addArguments("--disable-gpu"); // workaround just for Windows
        }
        webDriverManager
                .capabilities(options)
                .browserVersion("116")
                .setup();
        return webDriverManager.create();
    }
```