package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.*;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static java.util.logging.Level.ALL;
import static org.junit.jupiter.api.Assertions.*;
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
    private String sessionName = "Today's Session";
    private String sessionDescription = "Wow today session will be amazing";
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

        log.info("[INI setUP]");

        host = SetUp.getHost();
        users_data = loadStudentsData("src/test/resources/inputs/default_user_LoggedVideoStudents.csv");
        log.info("Test over url: " + host);

        //teacher setUp

        teachermail = "teacher@gmail.com";
        teacher_pass = "pass";
        BrowserUser bronteacher = UserLoader.setupBrowser("chrome", "BrowserTeacher", teachermail, 100, APP_URL, log);
        teacherDriver = bronteacher.getDriver();
        courseName = "Pseudoscientific course for treating the evil eye";


        //check if logged with correct user
        teacherDriver = SetUp.loginUser(teacherDriver, host, teachermail, teacher_pass);
        teacherDriver = UserUtilities.checkLogin(teacherDriver, teachermail);
        teacherName = UserUtilities.getUserName(teacherDriver, true, host);

        //students setUp
        studentsmails = new ArrayList<>();
        studentPass = new ArrayList<>();
        studentNames = new ArrayList<>();
        studentDriver = new ArrayList<>();

        String[] students_data = users_data.split(";");

        for (int i = 0; i < students_data.length; i++) {
            String studentemail = students_data[i].split(":")[0];
            studentsmails.add(studentemail);
            String studentpass = students_data[i].split(":")[1];
            studentPass.add(studentpass);

            //WebDriver studentD = UserLoader.allocateNewBrowser(students_data[i].split(":")[2]);
            BrowserUser studentD = setupBrowser("chrome", "BrowserStudent" + i, studentemail, 100);



            this.slowLogin(studentD, studentemail, studentpass);
            studentNames.add(userName);
            studentDriver.add(studentD);

        }

        /* Dedicated set up to Forum tests*/
	        /*log.info("INI dedicated setUP");


	    	//LOAD PROPERTIES:
	    	properties = new Properties();
			try {
				// load a properties file for reading
				properties.load(new FileInputStream("src/test/resources/inputs/test.properties"));
				courseName = properties.getProperty("forum.test.course");

			} catch (IOException ex) {
				ex.printStackTrace();
			}

	    	log.info("End dedicated setUP");*/
        /*END dedicated*/
        log.info("[End setUP]");
    }

    @AfterEach
    public void teardown() throws IOException {
        //TODO delete tested test if it is last test.
        SetUp.tearDown(teacherDriver);
        teacherDriver.close();
        for (BrowserUser driver : studentDriver) {
            driver.dispose();

        }
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @ParameterizedTest
    @MethodSource("data")
    public void sessionTest() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR);
        if (mHour == 0) mHour = 12;
        int mAMPM = calendar.get(Calendar.AM_PM);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);

        sessionDate = "" + (mDay < 10 ? "0" + mDay : mDay) + (mMonth < 10 ? "0" + mMonth : mMonth) + mYear;
        sessionHour = "" + (mHour < 10 ? "0" + mHour : mHour) + (mMinute < 10 ? "0" + mMinute : mMinute) + (mAMPM == Calendar.AM ? "A" : "P");
        try {
            if (!NavigationUtilities.amIHere(teacherDriver, COURSES_URL.replace("__HOST__", host))) {
                teacherDriver = NavigationUtilities.toCoursesHome(teacherDriver);
            }
            List<String> courses = CourseNavigationUtilities.getCoursesList(teacherDriver, host);

            assertTrue(courses.size() > 0, "No courses in the list");
            //Teacher go to Course and create a new session to join

            WebElement course = CourseNavigationUtilities.getCourseElement(teacherDriver, courseName);

            course.findElement(COURSELIST_COURSETITLE).click();
            Wait.notTooMuch(teacherDriver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
            teacherDriver = CourseNavigationUtilities.go2Tab(teacherDriver, SESSION_ICON);

            teacherDriver = Click.element(teacherDriver, SESSIONLIST_NEWSESSION_ICON);

            //wait for modal
            WebElement modal = Wait.notTooMuch(teacherDriver).until(ExpectedConditions.visibilityOfElementLocated(SESSIONLIST_NEWSESSION_MODAL));
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_TITLE).sendKeys(sessionName);
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_CONTENT).sendKeys(sessionDescription);
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_DATE).sendKeys(sessionDate);
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_TIME).sendKeys(sessionHour);
            teacherDriver = Click.element(teacherDriver, modal.findElement(SESSIONLIST_NEWSESSION_MODAL_POSTBUTTON));
            Wait.notTooMuch(teacherDriver);
            //teacherDriver = Click.element(teacherDriver, SESSIONLIST_NEWSESSION_MODAL_DATE);
            //check if session has been created
            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(teacherDriver);
            assertTrue(session_titles.contains(sessionName), "Session has not been created");

        } catch (ElementNotFoundException e) {
            fail("Error while creating new SESSION");
        }

        //Teacher Join Session
        try {

            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(teacherDriver);
            assertTrue(session_titles.contains(sessionName), "Session has not been created");

            //Teacher to: JOIN SESSION.
            WebElement session = SessionNavigationUtilities.getSession(teacherDriver, sessionName);
            teacherDriver = Click.element(teacherDriver, session.findElement(SESSIONLIST_SESSION_ACCESS));

            //assertTrue(condition);
            //Check why this is failing... maybe urls are not correct? configuration on the project?

        } catch (ElementNotFoundException e) {
            fail("Error while creating new SESSION");
        }

        //Students Join Sessions
        try {
            for (BrowserUser student_d : studentDriver) {

                WebDriver driverstudent = student_d.getDriver();
                if (!NavigationUtilities.amIHere(driverstudent, COURSES_URL.replace("__HOST__", host))) {
                    driverstudent = NavigationUtilities.toCoursesHome(driverstudent);
                }
                List<String> courses = CourseNavigationUtilities.getCoursesList(driverstudent, host);

                assertTrue(courses.size() > 0, "No courses in the list");
                //Teacher go to Course and create a new session to join

                WebElement course = CourseNavigationUtilities.getCourseElement(driverstudent, courseName);

                course.findElement(COURSELIST_COURSETITLE).click();
                Wait.notTooMuch(driverstudent).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
                driverstudent = CourseNavigationUtilities.go2Tab(driverstudent, SESSION_ICON);

                List<String> session_titles = SessionNavigationUtilities.getFullSessionList(driverstudent);
                assertTrue(session_titles.contains(sessionName), "Session has not been created");

                //Student to: JOIN SESSION.
                WebElement session = SessionNavigationUtilities.getSession(driverstudent, sessionName);
                driverstudent = Click.element(driverstudent, session.findElement(SESSIONLIST_SESSION_ACCESS));

                //assertTrue(condition);
                //Check why this is failing... maybe urls are not correct? configuration on the project?
            }

        } catch (ElementNotFoundException e) {
            fail("Error while creating new SESSION");
        }

        //Students Leave Sessions
        try {
            for (BrowserUser student : studentDriver) {
                WebDriver driverstudent = student.getDriver();
                Wait.notTooMuch(driverstudent);
                //student to: LEAVE SESSION.
                driverstudent = Click.element(driverstudent, SESSION_LEFT_MENU_BUTTON);

                driverstudent = Click.element(driverstudent, SESSION_EXIT_ICON);

                //Wait for something
                Wait.notTooMuch(driverstudent).until(ExpectedConditions.visibilityOfElementLocated(COURSE_TABS));
                //assertTrue(condition);
                //Check why this is failing... maybe urls are not correct? configuration on the project?
            }

        } catch (ElementNotFoundException e) {
            fail("Error while leaving SESSION");
        }
        //Teacher Leave Session
        try {

            //student to: LEAVE SESSION.
            teacherDriver = Click.element(teacherDriver, SESSION_LEFT_MENU_BUTTON);

            teacherDriver = Click.element(teacherDriver, SESSION_EXIT_ICON);

            //Wait for something
            Wait.notTooMuch(teacherDriver).until(ExpectedConditions.visibilityOfElementLocated(COURSE_TABS));
            //assertTrue(condition);
            //Check why this is failing... maybe urls are not correct? configuration on the project?

        } catch (ElementNotFoundException e) {
            fail("Error while leaving SESSION");
        }
        try {
            //delete session by teacher
            WebElement session = SessionNavigationUtilities.getSession(teacherDriver, sessionName);

            teacherDriver = Click.element(teacherDriver, session.findElement(SESSIONLIST_SESSIONEDIT_ICON));

            WebElement modal = Wait.notTooMuch(teacherDriver).until(ExpectedConditions.visibilityOfElementLocated(SESSIONLIST_EDIT_MODAL));
            teacherDriver = Click.element(teacherDriver, modal.findElement(SESSIONLIST_EDITMODAL_DELETE_DIV).findElement(By.tagName("label")));
            teacherDriver = Click.element(teacherDriver, modal.findElement(SESSIONLIST_EDITMODAL_DELETE_DIV).findElement(By.tagName("a")));

            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(teacherDriver);
            assertFalse(session_titles.contains(sessionName), "Session has not been deleted");

        } catch (ElementNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public String loadStudentsData(String path) {
        FileReader file;
        StringBuilder key = new StringBuilder();
        try {
            file = new FileReader(path);

            BufferedReader reader = new BufferedReader(file);

            // **** key is declared here in this block of code

            String line = reader.readLine();

            while (line != null) {
                key.append(line);
                line = reader.readLine();
            }
            System.out.println(key); // so key works
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return key.toString();
    }


}
