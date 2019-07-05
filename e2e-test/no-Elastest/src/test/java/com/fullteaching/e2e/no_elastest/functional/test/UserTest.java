package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.functional.test.media.FullTeachingTestE2E;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.UserLoader;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;


import static java.lang.invoke.MethodHandles.lookup;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
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
public class UserTest extends FullTeachingTestE2E {

	
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;
	private static String APP_URL;
	
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
	
	final static  Logger log = getLogger(lookup().lookupClass());
	
	BrowserUser usrbrowser;

	public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestUsers();
    }
	
	 @BeforeAll()
		static void setupAll() {
			
			if (System.getenv("ET_EUS_API") == null) {
				// Outside ElasTest
				System.setProperty("webdriver.chrome.driver",
			 	           "C:/chromedriver_win32/chromedriver.exe");
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
	
		@AfterEach
		void dispose(TestInfo info) {
			try {
				this.logout(usrbrowser);
				usrbrowser.dispose();
			} finally {
				log.info("##### Finish test: " +  info.getTestMethod().get().getName());
			}
		}
    /**
     * This test is a simple logging ackenoledgment, that checks if the current logged user
     * was logged correctly
     */ 
	@ParameterizedTest
	@MethodSource("data")
	public void loginTest(String user, String password, String role) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {
		
	//	driver = rwd;
		usrbrowser= UserLoader.setupBrowser("chrome",role,user,100,APP_URL,log);
		
		try {
		
			this.slowLogin(usrbrowser, user, password);
			UserUtilities.checkLogin(usrbrowser.getDriver(), user);

			assertTrue(true, "not logged");

		} catch (NotLoggedException | BadUserException e) {
				
			e.printStackTrace();
			fail("Not logged");
			
		} catch (ElementNotFoundException e) {
			
			e.printStackTrace();
			fail(e.getLocalizedMessage());
			
		} 
		
		try {
			this.logout(usrbrowser);
			
			UserUtilities.checkLogOut(usrbrowser.getDriver());
			
		} catch (ElementNotFoundException enfe) {
			fail("Still logged");
			
		}
			
		assertTrue(true);
	}
	
	
}
