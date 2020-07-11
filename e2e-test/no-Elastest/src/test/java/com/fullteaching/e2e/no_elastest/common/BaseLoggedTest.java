package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.fullteaching.e2e.no_elastest.common.Constants.LOCALHOST;
import static com.fullteaching.e2e.no_elastest.common.Constants.PORT;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.logging.Level.ALL;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.openqa.selenium.remote.CapabilityType.LOGGING_PREFS;
import static org.slf4j.LoggerFactory.getLogger;

//import io.github.bonigarcia.seljup.DriverCapabilities;

@ExtendWith(SeleniumExtension.class)
public class BaseLoggedTest {

    public static final String CHROME = "chrome";
    // For use another host
    //protected static final String host= SetUp.getHost();
    public static final String FIREFOX = "firefox";
    //protected common attributes
    protected static final String BROWSER_VERSION_LATEST = "latest";
    protected static String HOST = LOCALHOST;
    protected final static Logger log = getLogger(lookup().lookupClass());
    protected static String userName;
    protected static String usermail;
    protected static String password;
    protected static String APP_URL;
    protected static Properties properties;
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    private static String TEACHER_BROWSER;
    private static String STUDENT_BROWSER;
    protected BrowserUser user;
    //@DriverCapabilities
    ChromeOptions capabilities = new ChromeOptions();

    {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, ALL);
        capabilities.setCapability(LOGGING_PREFS, logPrefs);
    }


    public BaseLoggedTest() {
        if (System.getenv("ET_EUS_API") == null) {

            ChromeDriverManager.getInstance(chrome).setup();
            FirefoxDriverManager.getInstance(firefox).setup();
        }
        if (System.getenv("ET_SUT_HOST") != null) {
            APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":" + PORT + "/";
            //In order to check if is correct
            HOST = APP_URL;
        } else {
            APP_URL = System.getProperty("app.url");

            if (APP_URL == null) {
                APP_URL = LOCALHOST;
            } else {
                //In order to check id its correct
                HOST = APP_URL;
            }
        }
        properties = new Properties();
        try {
            // load a properties file for reading
            properties.load(new FileInputStream("src/test/resources/inputs/test.properties"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    @BeforeAll()
    static void setupAll() { //28 lines
        properties = new Properties();
        try {
            // load a properties file for reading
            properties.load(new FileInputStream("src/test/resources/inputs/test.properties"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (System.getenv("ET_EUS_API") == null) {
            // Outside ElasTest
            ChromeDriverManager.getInstance(chrome).setup();
            FirefoxDriverManager.getInstance(firefox).setup();
        }
        if (System.getenv("ET_SUT_HOST") != null) {
            APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":" + PORT + "/";
        } else {
            APP_URL = System.getProperty("app.url");
            if (APP_URL == null) {
                APP_URL = LOCALHOST;
            }
        }
        TEACHER_BROWSER = System.getenv("TEACHER_BROWSER");
        STUDENT_BROWSER = System.getenv("STUDENT_BROWSER");
        if ((TEACHER_BROWSER == null) || (!TEACHER_BROWSER.equals(FIREFOX))) {
            TEACHER_BROWSER = CHROME;
        }
        if ((STUDENT_BROWSER == null) || (!STUDENT_BROWSER.equals(FIREFOX))) {
            STUDENT_BROWSER = CHROME;
        }
        //log.info("Using URL {} to connect to openvidu-testapp", APP_URL);
    }

    protected BrowserUser setupBrowser(String browser, TestInfo testInfo,
                                       String userIdentifier, int secondsOfWait) {

        return this.setupBrowser(browser,
                testInfo.getTestMethod().get().getName(), userIdentifier,
                secondsOfWait);
    }

    protected BrowserUser setupBrowser(String browser, String testName,
                                       String userIdentifier, int secondsOfWait) {
        BrowserUser u;
        log.info("Starting browser ({})", browser);
        switch (browser) {
            case CHROME:
                u = new ChromeUser(userIdentifier, secondsOfWait, testName,
                        userIdentifier);
                break;
            case FIREFOX:
                u = new FirefoxUser(userIdentifier, secondsOfWait, testName,
                        userIdentifier);
                break;
            default:
                u = new ChromeUser(userIdentifier, secondsOfWait, testName,
                        userIdentifier);
        }
        log.info("Navigating to {}", APP_URL);
        u.getDriver().get(APP_URL);
        final String GLOBAL_JS_FUNCTION = "var s = window.document.createElement('script');"
                + "s.innerText = 'window.MY_FUNC = function(containerQuerySelector) {"
                + "var elem = document.createElement(\"div\");"
                + "elem.id = \"video-playing-div\";"
                + "elem.innerText = \"VIDEO PLAYING\";"
                + "document.body.appendChild(elem);"
                + "console.log(\"Video check function successfully added to DOM by Selenium\")}';"
                + "window.document.head.appendChild(s);";
        u.runJavascript(GLOBAL_JS_FUNCTION);
        u.getDriver().manage().window().maximize();
        return u;
    }

    @AfterEach
    void tearDown(TestInfo testInfo) throws IOException { //13 lines
        String testName = testInfo.getTestMethod().get().getName();
        if (user != null) {
            log.info("##### Finish test: {} - Driver {}", testName, this.user.getDriver());
            //  log.info("url:" + user.getDriver().getCurrentUrl() + "\nScreenshot (in Base64) at the end of the test:\n{}",
            //        SetUp.getBase64Screenshot(user.getDriver()));
            log.info("Browser console at the end of the test");
            LogEntries logEntries = user.getDriver().manage().logs().get(BROWSER);
            logEntries.forEach((entry) -> log.info("[{}] {} {}",
                    new Date(entry.getTimestamp()), entry.getLevel(),
                    entry.getMessage()));
            //TO-DO- ERROR with the logout
            //this.logout(user);
            user.dispose();
        }
    }


    protected void slowLogin(BrowserUser user, String userEmail,
                             String userPass) {//24 lines
        this.login(user, userEmail, userPass, true);
    }

    protected void quickLogin(BrowserUser user, String userEmail,
                              String userPass) { //24 lines
        this.login(user, userEmail, userPass, false);
    }

    private void login(BrowserUser user, String userEmail, String userPass,
                       boolean slow) { //24 lines
        log.info("Logging in user {} with mail '{}'", user.getClientData(), userEmail);
        Wait.waitForPageLoaded(user.getDriver());
        openDialog("#download-button", user);
        Wait.waitForPageLoaded(user.getDriver());
        // Find form elements (login modal is already opened)
        WebElement userNameField = user.getDriver().findElement(By.id("email"));
        WebElement userPassField = user.getDriver().findElement(By.id("password"));
        // Fill input fields
        userNameField.sendKeys(userEmail);
        if (slow)
            waitSeconds(1);
        userPassField.sendKeys(userPass);
        if (slow)
            waitSeconds(1);
        // Ensure fields contain what has been entered
        Assert.assertEquals(userNameField.getAttribute("value"), userEmail);
        Assert.assertEquals(userPassField.getAttribute("value"), userPass);
        user.getDriver().findElement(By.id("log-in-btn")).click();
        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("course-list"))), "Course list not present");
        try {
            userName = UserUtilities.getUserName(user.getDriver(), true, APP_URL);
        } catch (NotLoggedException | ElementNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("Logging in successful for user {}", user.getClientData());
    }

    protected void logout(BrowserUser user) { //43 lines
        //   log.info("Logging out {}", user.getClientData());
        if (user.getDriver().findElements(By.cssSelector("#fixed-icon"))
                .size() > 0) {
            // Get out of video session page
            if (!isClickable("#exit-icon", user)) { // Side menu not opened
                user.getDriver().findElement(By.cssSelector("#fixed-icon"))
                        .click();
                waitForAnimations();
            }
            user.getWaiter().until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("#exit-icon")));
            user.getDriver().findElement(By.cssSelector("#exit-icon")).click();
        }
        try {
            // Up bar menu
            user.getWaiter().withTimeout(1000, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#arrow-drop-down")));
            user.getDriver().findElement(By.cssSelector("#arrow-drop-down"))
                    .click();
            waitForAnimations();
            user.getWaiter().until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("#logout-button")));
            user.getDriver().findElement(By.cssSelector("#logout-button"))
                    .click();
        } catch (TimeoutException e) {
            // Shrunk menu
            user.getWaiter().withTimeout(1000, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("a.button-collapse")));
            user.getDriver().findElement(By.cssSelector("a.button-collapse"))
                    .click();
            waitForAnimations();
            user.getWaiter().until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='nav-mobile']//a[text() = 'Logout']")));
            user.getDriver()
                    .findElement(By.xpath(
                            "//ul[@id='nav-mobile']//a[text() = 'Logout']"))
                    .click();
        }

        log.info("Logging out successful for {}", user.getClientData());

        waitSeconds(1);
    }

    private boolean isClickable(String selector, BrowserUser user) {
        try {
            WebDriverWait wait = new WebDriverWait(user.getDriver(), 1);
            wait.until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector(selector)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void openDialog(String cssSelector, BrowserUser user) {

        log.debug("User {} opening dialog by clicking CSS '{}'",
                user.getClientData(), cssSelector);

        user.waitUntil(
                ExpectedConditions
                        .elementToBeClickable(By.cssSelector(cssSelector)),
                "Button for opening the dialog not clickable");

        user.getDriver().findElement(By.cssSelector(cssSelector)).click();
        Wait.waitForPageLoaded(user.getDriver());
        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")),
                "Dialog not opened");

        log.debug("Dialog opened for user {}", user.getClientData());
    }

    protected void openDialog(WebElement el, BrowserUser user) {//8lines
        log.debug("User {} opening dialog by web element '{}'",
                user.getClientData(), el);
        user.waitUntil(ExpectedConditions.elementToBeClickable(el),
                "Button for opening the dialog not clickable");
        el.click();
        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")),
                "Dialog not opened");
        log.debug("Dialog opened for user {}", user.getClientData());
    }

    protected void waitForDialogClosed(String dialogId, String errorMessage,
                                       BrowserUser user) {//14 lines
        log.debug("User {} waiting for dialog with id '{}' to be closed",
                user.getClientData(), dialogId);
        user.waitUntil(ExpectedConditions
                        .presenceOfElementLocated(By.xpath("//div[@id='" + dialogId
                                + "' and contains(@class, 'my-modal-class') and contains(@style, 'opacity: 0') and contains(@style, 'display: none')]")),
                "Dialog not closed. Reason: " + errorMessage);
        user.waitUntil(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".modal.my-modal-class.open")),
                "Dialog not closed. Reason: " + errorMessage);
        user.waitUntil(
                ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector(".modal-overlay"), 0),
                "Dialog not closed. Reason: " + errorMessage);
        log.debug("Dialog closed for user {}", user.getClientData());
    }

    protected void waitForAnimations() {
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void waitSeconds(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}