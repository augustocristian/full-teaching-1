package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SpiderNavigation;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class LoggedLinksTests extends BaseLoggedTest {
	
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;
	
	
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
	
	private static String APP_URL;
	protected static int DEPTH = 3;
	
	final static  Logger log = getLogger(lookup().lookupClass());

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
    /**
     * This test get logged the user and checks the navigation by URL works correctly.First
     * get all the possible URLS for the current user for after it iterate over them checking
     * that the response of the rest service was KO
     * 
     */ 
	@ParameterizedTest
	@MethodSource("data")
	public void spiderLoggedTest(String user, String password, String role)  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

	//	driver = rwd;
		
		driver= UserLoader.setupBrowser("chrome",role,user,100);
		driver = loginAndValidate(driver,user,password);

		/*navigate from home*/
		NavigationUtilities.getUrlAndWaitFooter(driver, host);
				
		List <WebElement> pageLinks = SpiderNavigation.getPageLinks(driver);
		
		Map <String,String> explored = new HashMap<String,String>();
		
		//Navigate the links... 
		//Problem: once one is pressed the rest will be unusable as the page reloads... 

		explored = SpiderNavigation.exploreLinks(driver, pageLinks, explored, DEPTH);
		
		List<String> failed_links = new ArrayList<String>();
		System.out.println(user+" tested "+explored.size()+" urls");
		explored.forEach((link,result) -> {
				log.debug("\t"+link+" => "+result);
				if (result.equals("KO")) {
					failed_links.add(link);				
				}			
		});

		String msg = "";
		for (String failed: failed_links) {
			msg = failed +"\n";	
		}
		assertTrue(failed_links.isEmpty(), msg);
	}
	
}
