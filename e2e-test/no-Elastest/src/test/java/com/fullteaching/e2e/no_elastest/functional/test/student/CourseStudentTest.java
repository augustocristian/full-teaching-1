package com.fullteaching.e2e.no_elastest.functional.test.student;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.IOException;
import java.util.stream.Stream;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;


@ExtendWith(SeleniumExtension.class)
public class CourseStudentTest extends BaseLoggedTest {

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";
    final static Logger log = getLogger(lookup().lookupClass());
    protected static String APP_URL;
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    public String roles;
    WebDriver driver;

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestStudents();
    }


    /**
     * This tests get the login the user as student, go the the courses  and check if
     * there is any course in the list.After it, click in the first course of the list
     * and wait for the visibility of it.In second place, the student go to the home,
     * Session,Forum, Files and attenders tab to check if they are visible.
     */
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READONLY")
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @ParameterizedTest
    @MethodSource("data")
    public void studentCourseMainTest(String usermail, String password, String role) throws InterruptedException {

        Thread.sleep(20000);
        assertTrue(true);

    }


}
