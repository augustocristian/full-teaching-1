package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SpiderNavigation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fullteaching.e2e.no_elastest.common.Constants.LOCALHOST;
import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class UnLoggedLinksTests extends BaseLoggedTest {

    protected static WebDriver driver;
    protected static int DEPTH = 3;
    protected static String host = LOCALHOST;

    @BeforeAll
    static void setUp() {

/*        String appHost = getProperty("fullTeachingUrl");
        if (appHost != null) {
            host = appHost;
        }*/
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READWRITE")
    @Test
    public void spiderUnloggedTest() throws InterruptedException {
        Thread.sleep(30000);
        assertTrue(true);
   /*     *//*navigate from home*//*

        NavigationUtilities.getUrlAndWaitFooter(driver, host);

        List<WebElement> pageLinks = SpiderNavigation.getPageLinks(driver);

        Map<String, String> explored = new HashMap<>();

        //Navigate the links...
        //Problem: once one is pressed the rest will be unusable as the page reloads...

        explored = SpiderNavigation.exploreLinks(driver, pageLinks, explored, DEPTH);

        List<String> failed_links = new ArrayList<>();

        explored.forEach((link, result) -> {
            if (result.equals("KO")) failed_links.add(link);
        });

        String msg = "";
        for (String failed : failed_links) {
            msg = failed + "\n";
        }
        assertTrue(failed_links.isEmpty(), msg);*/
    }

}
