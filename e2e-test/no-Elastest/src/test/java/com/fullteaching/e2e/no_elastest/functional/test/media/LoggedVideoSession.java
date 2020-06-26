package com.fullteaching.e2e.no_elastest.functional.test.media;

import com.fullteaching.e2e.no_elastest.common.*;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.remote.DesiredCapabilities.chrome;
import static org.slf4j.LoggerFactory.getLogger;

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
    protected BrowserUser user;
    //at least 1 student;
    protected List<BrowserUser> studentDriver;
    protected String teacherName;
    protected String teachermail;
    protected String teacher_pass;
    protected List<String> studentsmails;
    protected List<String> studentPass;
    protected List<String> studentNames;
    protected List<String> students;
    protected String host = LOCALHOST;
    private static String TEACHER_BROWSER;
    private static String STUDENT_BROWSER;
    protected Properties properties;
    //@DriverCapabilities
    DesiredCapabilities capabilities = chrome();
    private String sessionDate;
    private String sessionHour;


    public static Collection<String[]> data() throws IOException {
        return ParameterLoader.sessionParameters();
    }


    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @Test
    public void sessionTest() { // 160 +225+ 28 set up +13 lines teardown =426
        courseName = "Pseudoscientific course for treating the evil eye";
        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();
        log.info("##### Start test: " + testName);
        TEACHER_BROWSER = CHROME;
        users_data = loadStudentsData("src/test/resources/inputs/default_user_LoggedVideoStudents.csv");
        this.user = setupBrowser(TEACHER_BROWSER, testName, "Teacher", 30);//27 lines
        this.slowLogin(user, "teacher@gmail.com", "pass");//24 lines
        //students setUp
        students = new ArrayList<String>();
        studentPass = new ArrayList<String>();
        studentNames = new ArrayList<String>();
        studentDriver = new ArrayList<BrowserUser>();
        String[] students_data = users_data.split(";");
        for (int i = 0; i < students_data.length; i++) {
            String userid = students_data[i].split(":")[0];
            students.add(userid);
            String userpass = students_data[i].split(":")[1];
            studentPass.add(userpass);
            STUDENT_BROWSER = students_data[i].split(":")[2];
            //WebDriver studentD = UserLoader.allocateNewBrowser(students_data[i].split(":")[2]);
            BrowserUser studentD = setupBrowser(STUDENT_BROWSER, testName, "Student" + i, 30); //27 lines
            this.slowLogin(studentD, userid, userpass); //24 lines
            studentNames.add(userid);
            studentDriver.add(studentD);
        }
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
            //if (!NavigationUtilities.amIHere(user.getDriver(), COURSES_URL.replace("__HOST__", host))) {
            //    NavigationUtilities.toCoursesHome(user.getDriver());
            //  }
            List<String> courses = CourseNavigationUtilities.getCoursesList(user.getDriver(), host); //13 lines
            assertTrue(courses.size() > 0, "No courses in the list");
            //Teacher go to Course and create a new session to join
            WebElement course = CourseNavigationUtilities.getCourseElement(user.getDriver(), courseName); //14 lines
            course.findElement(COURSELIST_COURSETITLE).click();
            Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
            CourseNavigationUtilities.go2Tab(user.getDriver(), SESSION_ICON); //4 lines
            Click.element(user.getDriver(), SESSIONLIST_NEWSESSION_ICON);
            //wait for modal
            WebElement modal = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SESSIONLIST_NEWSESSION_MODAL));
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_TITLE).sendKeys(sessionName);
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_CONTENT).sendKeys(sessionDescription);
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_DATE).sendKeys(sessionDate);
            modal.findElement(SESSIONLIST_NEWSESSION_MODAL_TIME).sendKeys(sessionHour);
            Click.element(user.getDriver(), modal.findElement(SESSIONLIST_NEWSESSION_MODAL_POSTBUTTON));
            Wait.notTooMuch(user.getDriver());
            //teacherDriver = Click.element(teacherDriver, SESSIONLIST_NEWSESSION_MODAL_DATE);
            //check if session has been created
            Wait.waitForPageLoaded(user.getDriver()); //13 lines
            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver());
            assertTrue(session_titles.contains(sessionName), "Session has not been created");
        } catch (ElementNotFoundException e) {
            fail("Error while creating new SESSION");
        }
        //Teacher Join Session
        try {
            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver());
            assertTrue(session_titles.contains(sessionName), "Session has not been created");
            //Teacher to: JOIN SESSION.
            WebElement session = SessionNavigationUtilities.getSession(user.getDriver(), sessionName); //17 lines
            Click.element(user.getDriver(), session.findElement(SESSIONLIST_SESSION_ACCESS));
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
                    driverstudent = NavigationUtilities.toCoursesHome(driverstudent); //3lines
                }
                List<String> courses = CourseNavigationUtilities.getCoursesList(driverstudent, host);//13 lines
                assertTrue(courses.size() > 0, "No courses in the list");
                //Teacher go to Course and create a new session to join
                WebElement course = CourseNavigationUtilities.getCourseElement(driverstudent, courseName); //14 lines
                course.findElement(COURSELIST_COURSETITLE).click();
                Wait.notTooMuch(driverstudent).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
                driverstudent = CourseNavigationUtilities.go2Tab(driverstudent, SESSION_ICON);//4lines
                List<String> session_titles = SessionNavigationUtilities.getFullSessionList(driverstudent);//7 lines
                assertTrue(session_titles.contains(sessionName), "Session has not been created");
                //Student to: JOIN SESSION.
                WebElement session = SessionNavigationUtilities.getSession(driverstudent, sessionName);//17 lines
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
                Wait.notTooMuch(driverstudent).until(ExpectedConditions.visibilityOfElementLocated(SESSION_EXIT_ICON));
                WebElement buttonexit = driverstudent.findElement(SESSION_EXIT_ICON);
                JavascriptExecutor executor = (JavascriptExecutor) driverstudent;
                executor.executeScript("arguments[0].click();", buttonexit);
                // driverstudent.findElement(By.id("exit-icon")).click();
                //  driverstudent = Click.element(driverstudent, By.className("right material-icons video-icon"));
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
            Click.element(user.getDriver(), SESSION_LEFT_MENU_BUTTON);
            WebElement buttonexit = user.getDriver().findElement(SESSION_EXIT_ICON);
            JavascriptExecutor executor = (JavascriptExecutor) user.getDriver();
            executor.executeScript("arguments[0].click();", buttonexit);
            Wait.waitForPageLoaded(user.getDriver());//13 lines
            //Wait for something
            Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(COURSE_TABS));
            //assertTrue(condition);
            //Check why this is failing... maybe urls are not correct? configuration on the project?
        } catch (ElementNotFoundException e) {
            fail("Error while leaving SESSION");
        }
        try {
            //delete session by teacher
            WebElement session = SessionNavigationUtilities.getSession(user.getDriver(), sessionName);
            Click.element(user.getDriver(), session.findElement(SESSIONLIST_SESSIONEDIT_ICON));
            WebElement modal = Wait.notTooMuch(user.getDriver()).until(ExpectedConditions.visibilityOfElementLocated(SESSIONLIST_EDIT_MODAL));
            Click.element(user.getDriver(), modal.findElement(SESSIONLIST_EDITMODAL_DELETE_DIV).findElement(By.tagName("label")));
            Click.element(user.getDriver(), modal.findElement(SESSIONLIST_EDITMODAL_DELETE_DIV).findElement(By.tagName("a")));
            Wait.waitForPageLoaded(user.getDriver());//13 lines
            List<String> session_titles = SessionNavigationUtilities.getFullSessionList(user.getDriver()); //7 lines
            assertFalse(session_titles.contains(sessionName), "Session has not been deleted");
        } catch (ElementNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//Logout and exit students
        for (BrowserUser student : studentDriver) {
            this.logout(student);
            student.dispose();
        }
        //logout and exist teacher
        this.logout(user);
        user.dispose();
    }

    public String loadStudentsData(String path) { //17 lines
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
