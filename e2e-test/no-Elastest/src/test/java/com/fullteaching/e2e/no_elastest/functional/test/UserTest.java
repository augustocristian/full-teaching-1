package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.UserLoader;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;


import static java.lang.invoke.MethodHandles.lookup;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.stream.Stream;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;

import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;



@ExtendWith(SeleniumExtension.class)
public class UserTest extends BaseLoggedTest {

	
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;
	private static String APP_URL;
	
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
	
	final static  Logger log = getLogger(lookup().lookupClass());
	

	public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }
	

	
    /**
     * This test is a simple logging ackenoledgment, that checks if the current logged user
     * was logged correctly
     */ 
	@ParameterizedTest
	@MethodSource("data")
	public void loginTest(String user, String password, String role) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {
		BrowserUser usrbrowser;
	//	driver = rwd;
		usrbrowser= UserLoader.setupBrowser("chrome",role,user,100,APP_URL,log);
		driver=usrbrowser.getDriver();
		try {
			driver = UserUtilities.login(driver, user, password, host);
		
			driver = UserUtilities.checkLogin(driver, user);

			assertTrue(true, "not logged");

		} catch (NotLoggedException | BadUserException e) {
				
			e.printStackTrace();
			fail("Not logged");
			
		} catch (ElementNotFoundException e) {
			
			e.printStackTrace();
			fail(e.getLocalizedMessage());
			
		}  catch (TimeOutExeception e) {
			fail(e.getLocalizedMessage());
		} 
		
		try {
			driver = UserUtilities.logOut(driver,host);
			
			driver = UserUtilities.checkLogOut(driver);
			
		} catch (ElementNotFoundException enfe) {
			fail("Still logged");
			
		} catch (NotLoggedException e) {
			assertTrue(true, "Not logged");
		}
			
		assertTrue(true);
	}
	
	
}
