package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import io.github.bonigarcia.SeleniumExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@ExtendWith(SeleniumExtension.class)
public class UserTest extends BaseLoggedTest {


    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    WebDriver driver;

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }


    /**
     * This test is a simple logging ackenoledgment, that checks if the current logged user
     * was logged correctly
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @ParameterizedTest
    @MethodSource("data")
    public void loginTest(String usermail, String password, String role) {

        user = setupBrowser("chrome", role, usermail, 100);
        driver = user.getDriver();
        try {
            this.slowLogin(user, usermail, password);

            driver = UserUtilities.checkLogin(driver, usermail);

            assertTrue(true, "not logged");

        } catch (NotLoggedException | BadUserException e) {

            e.printStackTrace();
            fail("Not logged");

        } catch (ElementNotFoundException e) {

            e.printStackTrace();
            fail(e.getLocalizedMessage());

        }

        try {
            driver = UserUtilities.logOut(driver, host);

            driver = UserUtilities.checkLogOut(driver);

        } catch (ElementNotFoundException enfe) {
            fail("Still logged");

        } catch (NotLoggedException e) {
            assertTrue(true, "Not logged");
        }

        assertTrue(true);
    }


}
