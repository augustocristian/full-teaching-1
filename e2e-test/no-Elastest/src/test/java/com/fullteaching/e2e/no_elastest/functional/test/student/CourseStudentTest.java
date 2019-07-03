package com.fullteaching.e2e.no_elastest.functional.test.student;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.DOMMannager;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.UserLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;


import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import com.fullteaching.e2e.no_elastest.common.ChromeUser;
import com.fullteaching.e2e.no_elastest.common.FirefoxUser;

import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;



@ExtendWith(SeleniumExtension.class)
public class CourseStudentTest extends BaseLoggedTest {
	
	public String roles;
    protected static String APP_URL;

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    
    private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;
	
	

    final static Logger log = getLogger(lookup().lookupClass());

    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestStudents();
    }
    
    @BeforeAll()
	static void setupAll() {
		System.setProperty("webdriver.chrome.driver",
 	           "C:/chromedriver_win32/chromedriver.exe");
		if (System.getenv("ET_EUS_API") == null) {
			// Outside ElasTest
			ChromeDriverManager.getInstance(chrome).setup();
			FirefoxDriverManager.getInstance(firefox).setup();
			
		}

		if (System.getenv("ET_SUT_HOST") != null) {
			APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":"+PORT+"/";
		} else {
			APP_URL = System.getProperty("app.url");
			if (APP_URL == null) {
				APP_URL = LOCALHOST;
			}
		}

		TEACHER_BROWSER = System.getenv("TEACHER_BROWSER");
		STUDENT_BROWSER = System.getenv("STUDENT_BROWSER");

		if ((TEACHER_BROWSER == null) || (!TEACHER_BROWSER.equals(FIREFOX))) {
			TEACHER_BROWSER = CHROME;
		}

		if ((STUDENT_BROWSER == null) || (!STUDENT_BROWSER.equals(FIREFOX))) {
			STUDENT_BROWSER = CHROME;
		}

		log.info("Using URL {} to connect to openvidu-testapp", APP_URL);
	}
    
    /**
     * This tests get the login the user as student, go the the courses  and check if 
     * there is any course in the list.After it, click in the first course of the list 
     * and wait for the visibility of it.In second place, the student go to the home,
     * Session,Forum, Files and attenders tab to check if they are visible.
     */ 
    @ParameterizedTest
	@MethodSource("data")
    public void studentCourseMainTest(String user, String password, String role)throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {
    	
    	driver= UserLoader.setupBrowser("chrome",role,user,100);
    	
		driver = loginAndValidate(driver,  user, password);

    	try {
    		if(!NavigationUtilities.amIHere(driver,COURSES_URL.replace("__HOST__", host)))
        		driver = NavigationUtilities.toCoursesHome(driver);
	    	
    		//go to first course
    		//get course list
    		List<String>course_list = CourseNavigationUtilities.getCoursesList(driver, host);
    		if (course_list.size()<0)  fail("No courses available for test user");
    		
    		WebElement course_button = CourseNavigationUtilities.getCourseElement(driver, course_list.get(0)).findElement(By.className("title"));
    			    	
	    	driver = Click.element(driver, course_button);
	    	
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(COURSE_TABS_TAG)));
	    	
    	}catch(Exception e) {
			fail("Failed to load Courses Tabs"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	//Check tabs
    	//Home tab 
    	try {
    
    		//WebDriverWait wait = new WebDriverWait(driver, 10); 
    		//wait.until(ExpectedConditions.presenceOfElementLocated(By.id(HOME_ICON_ID)));
    	
    		driver = CourseNavigationUtilities.go2Tab(driver, HOME_ICON);
    		
    		
    		
    	} catch(Exception e) {
    		fail("Failed to load home tab" + e.getClass() + ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, SESSION_ICON);
    	} catch(Exception e) {
    		fail("Failed to load session tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
    	} catch(Exception e) {
    		fail("Failed to load forum tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, FILES_ICON);
    	} catch(Exception e) {
    		fail("Failed to load files tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON);	
    	} catch(Exception e) {
    		fail("Failed to load attenders tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
 

    	
    }
    


}
