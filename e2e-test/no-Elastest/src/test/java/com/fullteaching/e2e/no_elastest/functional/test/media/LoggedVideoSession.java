package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static com.fullteaching.e2e.no_elastest.common.Constants.LOCALHOST;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.logging.Level.ALL;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.openqa.selenium.remote.CapabilityType.LOGGING_PREFS;
import static org.openqa.selenium.remote.DesiredCapabilities.chrome;
import static org.slf4j.LoggerFactory.getLogger;

//@Disabled
public class LoggedVideoSession extends BaseLoggedTest {

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    final static Logger log = getLogger(lookup().lookupClass());
    protected static BrowserUser teacher;
    private final String sessionName = "Today's Session";
    private final String sessionDescription = "Wow today session will be amazing";
    public String teacher_data;
    public String users_data;
    public String courseName;
    //1 teacher
    protected WebDriver teacherDriver;
    //at least 1 student;
    protected List<BrowserUser> studentDriver;
    protected String teacherName;
    protected String teachermail;
    protected String teacher_pass;
    protected List<String> studentsmails;
    protected List<String> studentPass;
    protected List<String> studentNames;
    protected String host = LOCALHOST;
    protected Properties properties;
    //@DriverCapabilities
    DesiredCapabilities capabilities = chrome();
    private String sessionDate;
    private String sessionHour;

    {
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(BROWSER, ALL);
        capabilities.setCapability(LOGGING_PREFS, logPrefs);
    }

    public static Collection<String[]> data() throws IOException {
        return ParameterLoader.sessionParameters();
    }

    @BeforeEach
    public void setUp() throws BadUserException, ElementNotFoundException, NotLoggedException, TimeOutExeception {


    }

    @AfterEach
    public void teardown() throws IOException {


    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @ParameterizedTest
    @MethodSource("data")
    public void sessionTest() throws InterruptedException {
        Thread.sleep(40000);
        assertTrue(true);
    }


}
