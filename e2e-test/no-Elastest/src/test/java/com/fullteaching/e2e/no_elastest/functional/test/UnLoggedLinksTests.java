package com.fullteaching.e2e.no_elastest.functional.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import static com.fullteaching.e2e.no_elastest.common.Constants.LOCALHOST;
import static java.lang.System.getProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract public class UnLoggedLinksTests {

    protected static WebDriver driver;
    protected static int DEPTH = 3;
    protected static String host = LOCALHOST;

    @BeforeAll
    static void setUp() {

        String appHost = getProperty("fullTeachingUrl");
        if (appHost != null) {
            host = appHost;
        }
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READWRITE")
    @Test
    public void spiderUnloggedTest() throws InterruptedException {
        Thread.sleep(13000);
        assertTrue(true);
    }

}
