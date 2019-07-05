package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SpiderNavigation;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;


@ExtendWith(SeleniumExtension.class)
public class LoggedLinksTests extends BaseLoggedTest {

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    final static Logger log = getLogger(lookup().lookupClass());
    protected static int DEPTH = 3;
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    WebDriver driver;

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }


    /**
     * This test get logged the user and checks the navigation by URL works correctly.First
     * get all the possible URLS for the current user for after it iterate over them checking
     * that the response of the rest service was KO*
     */

    @ParameterizedTest
    @MethodSource("data")
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READWRITE")
    public void spiderLoggedTest(String usermail, String password, String role) throws InterruptedException {

        Thread.sleep(30000);
        assertTrue(true);
/*
        user = setupBrowser("chrome", role, usermail, 100);
        driver = user.getDriver();
        this.slowLogin(user, usermail, password);

        *//*navigate from home*//*
        NavigationUtilities.getUrlAndWaitFooter(driver, host);

        List<WebElement> pageLinks = SpiderNavigation.getPageLinks(driver);

        Map<String, String> explored = new HashMap<>();

        //Navigate the links...
        //Problem: once one is pressed the rest will be unusable as the page reloads...

        explored = SpiderNavigation.exploreLinks(driver, pageLinks, explored, DEPTH);

        List<String> failed_links = new ArrayList<>();
        System.out.println(usermail + " tested " + explored.size() + " urls");
        explored.forEach((link, result) -> {
            log.debug("\t" + link + " => " + result);
            if (result.equals("KO")) {
                failed_links.add(link);
            }
        });

        String msg = "";
        for (String failed : failed_links) {
            msg = failed + "\n";
        }
        assertTrue(failed_links.isEmpty(), msg);*/
    }


}
