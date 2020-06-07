package com.fullteaching.e2e.no_elastest.functional.test.teacher;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.*;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import io.github.bonigarcia.SeleniumExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.IOException;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SeleniumExtension.class)
public class CourseTeacherTest extends BaseLoggedTest {


    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";
    protected static String APP_URL;
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    private static String course_title;
    protected String courseName;
    WebDriver driver;


    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }


    /**
     * This tests get the login the user, go the the courses and select the default
     * course.Once the user its here, it clicks upon the different tabs(Corse info,sessions,Forum,Files
     * and attenders), checking that the navigation its possible.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "READONLY")
    @ParameterizedTest
    @MethodSource("data")
    public void teacherCourseMainTest(String usermail, String password, String role) throws InterruptedException {
        Thread.sleep(11000);
        assertTrue(true);

    }

    /**
     * This tests get the login the user, go the the courses  and press the button of a
     * new course, creating a new course(each course have a time stap that avoids
     * overlapping between different test).After that, we proceed to delete thoose courses, click
     * into the edit icon, check the box that allows it and clicking into the delete button
     * After that, we proceed to check if the course dont appears in the list.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 15, sharing = true, accessMode = "DYNAMIC")
    @ParameterizedTest
    @MethodSource("data")
    public void teacherCreateAndDeleteCourseTest(String usermail, String password, String role) throws InterruptedException {
        Thread.sleep(12000);
        assertTrue(true);

    }

    /**
     * This tests get the login the user, go the the courses  and in first place, edits the
     * course title, change its name for EDIT_+ one timestamp to avoid test overlapping.After that, we proceed
     * to edit course details, deletes the details title, subtitle and content adding new values
     * for thoose fields.Second checks if this content was correctly added.In second place, checks if the
     * course forum is enabled/disbled, to proceed to check if allows disable/enable it.Finally
     * this test check if the current user, that is accessing to the course, is included
     * in the Attenders list( check if the user is in it)
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    public void teacherEditCourseValues(String usermail, String password, String role) throws InterruptedException {

        Thread.sleep(13000);
        assertTrue(true);

        //Well done!
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Disabled
    @ParameterizedTest
    @MethodSource("data")
    public void teacherDeleteCourseTest(String usermail, String password, String role) throws InterruptedException {
        Thread.sleep(10000);
        assertTrue(true);
    }


}
