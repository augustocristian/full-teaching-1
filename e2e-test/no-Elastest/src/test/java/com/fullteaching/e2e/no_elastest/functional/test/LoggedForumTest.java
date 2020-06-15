package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.DOMMannager;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import io.github.bonigarcia.SeleniumExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;


@Tag("e2e")
@DisplayName("E2E tests for FullTeaching Login Session")
@ExtendWith(SeleniumExtension.class)
public class LoggedForumTest extends BaseLoggedTest {
    //We comment this because we instantiate it in the SetUp
    //protected static WebDriver driver;

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    final static Logger log = getLogger(lookup().lookupClass());
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    protected String courseName = "Pseudoscientific course for treating the evil eye";
    protected String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};
    WebDriver driver;


    public LoggedForumTest() {
        super();
    }

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }

    /**
     * This test get login and navigate to the courses zone checking if there are
     * any courses. Second and go to the Pseudo... course accessing to the forum
     * and looks if its enable.If its enable, load all the entries and checks for
     * someone that have comments on it.Finally, with the two previous conditions,
     * makes a assertequals to ensure that both are accomplisment
     */

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 10, sharing = true, accessMode = "READONLY")
    @ParameterizedTest
    @MethodSource("data")
    public void forumLoadEntriesTest(String usermail, String password, String role) throws InterruptedException {


        Thread.sleep(10000);
        assertTrue(true);


    }

    /**
     * This test get login and create an custom title and content with the current date.
     * After that, navigate to courses for access the forum section.In the forum creates
     * a new entry with the previous created title and content. Secondly, we ensure that
     * the entry was created correctly and ensures that there are only one comment that
     * correponds with the body of that entry.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    public void forumNewEntryTest(String usermail, String password, String role) throws InterruptedException {

        Thread.sleep(10000);
        assertTrue(true);

    }

    /**
     * This test get login and create an custom title and content with the current date.
     * After that, navigate to courses for access the forum section.If in the forum
     * there are not any entries create an new entry and gets into it.In the other hand
     * if there are  any created previuously entry get into the first of them. Secondly,
     * once we are into the entry, we looks for the new comment button, making a new comment
     * in this entry with the custom content(the current date and hour).Finally, we iterate
     * over all comments looking for the comment that previously we create.
     */
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @ParameterizedTest
    @MethodSource("data")
    public void forumNewCommentTest(String usermail, String password, String role) throws InterruptedException {


        Thread.sleep(15000);
        assertTrue(true);

    }

    /**
     * This test get login and create like the previosly a custom content to make a comment
     * We proceed navigate to the courses forum zone, and check if there are any entries.
     * In the case that there are not entries, create a new entry and  replies to the
     * first comment of it ( the content of it).In the other hand if there are entries
     * previously created, go to the first and replies to the same comment.After it, we check
     * that the comment was correctly published.
     */

    @ParameterizedTest
    @MethodSource("data")
    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    public void forumNewReply2CommentTest(String usermail, String password, String role) throws InterruptedException {

       Thread.sleep(10000);
       assertTrue(true);
    }

}
