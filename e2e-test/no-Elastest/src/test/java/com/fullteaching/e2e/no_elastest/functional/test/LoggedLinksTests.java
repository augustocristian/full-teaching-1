package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.SpiderNavigation;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.UserLoader;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;


import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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
			ChromeDriverManager.getInstance(chrome).setup();
			FirefoxDriverManager.getInstance(firefox).setup();
		}

		if (System.getenv("ET_SUT_HOST") != null) {
			APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":5000/";
		} else {
			APP_URL = System.getProperty("app.url");
			if (APP_URL == null) {
				APP_URL = "https://localhost:5000/";
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

/*	@AfterEach
	void dispose(TestInfo info) {
		try {
			this.logout(user);
			user.dispose();
		} finally {
			log.info("##### Finish test: " +  info.getTestMethod().get().getName());
		}
	}*/

    /**
     * This test get logged the user and checks the navigation by URL works correctly.First
     * get all the possible URLS for the current user for after it iterate over them checking
     * that the response of the rest service was KO
     * 
     */ 
	@ParameterizedTest
	@MethodSource("data")
	public void spiderLoggedTest(String user, String password, String role)  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

		BrowserUser usrbrowser;
		
		usrbrowser= UserLoader.setupBrowser("chrome",role,user,100,APP_URL,log);
		driver=usrbrowser.getDriver();
		this.slowLogin(usrbrowser, user, password);

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
	
	 protected void slowLogin(BrowserUser user, String userEmail,
	            String userPass) {
	        this.login(user, userEmail, userPass, true);
	    }

	    protected void quickLogin(BrowserUser user, String userEmail,
	            String userPass) {
	        this.login(user, userEmail, userPass, false);
	    }

	    private void login(BrowserUser user, String userEmail, String userPass,
	            boolean slow) {

	        log.info("Logging in user {} with mail '{}'", user.getClientData(),
	                userEmail);

	        openDialog("#download-button", user);

	        // Find form elements (login modal is already opened)
	        WebElement userNameField = user.getDriver().findElement(By.id("email"));
	        WebElement userPassField = user.getDriver()
	                .findElement(By.id("password"));

	        // Fill input fields
	        userNameField.sendKeys(userEmail);
	        if (slow)
	            waitSeconds(1);
	        userPassField.sendKeys(userPass);
	        if (slow)
	            waitSeconds(1);

	        // Ensure fields contain what has been entered
	        Assert.assertEquals(userNameField.getAttribute("value"), userEmail);
	        Assert.assertEquals(userPassField.getAttribute("value"), userPass);

	        user.getDriver().findElement(By.id("log-in-btn")).click();

	        user.waitUntil(
	                ExpectedConditions.elementToBeClickable(By.id(("course-list"))),
	                "Course list not present");

	        log.info("Logging in successful for user {}", user.getClientData());
	    }

	    protected void logout(BrowserUser user) {

	        log.info("Logging out {}", user.getClientData());

	        if (user.getDriver().findElements(By.cssSelector("#fixed-icon"))
	                .size() > 0) {
	            // Get out of video session page
	            if (!isClickable("#exit-icon", user)) { // Side menu not opened
	                user.getDriver().findElement(By.cssSelector("#fixed-icon"))
	                        .click();
	                waitForAnimations();
	            }
	            user.getWaiter().until(ExpectedConditions
	                    .elementToBeClickable(By.cssSelector("#exit-icon")));
	            user.getDriver().findElement(By.cssSelector("#exit-icon")).click();
	        }
	        try {
	            // Up bar menu
	            user.getWaiter().withTimeout(1000, TimeUnit.MILLISECONDS)
	                    .until(ExpectedConditions.visibilityOfElementLocated(
	                            By.cssSelector("#arrow-drop-down")));
	            user.getDriver().findElement(By.cssSelector("#arrow-drop-down"))
	                    .click();
	            waitForAnimations();
	            user.getWaiter().until(ExpectedConditions
	                    .elementToBeClickable(By.cssSelector("#logout-button")));
	            user.getDriver().findElement(By.cssSelector("#logout-button"))
	                    .click();
	        } catch (TimeoutException e) {
	            // Shrunk menu
	            user.getWaiter().withTimeout(1000, TimeUnit.MILLISECONDS)
	                    .until(ExpectedConditions.visibilityOfElementLocated(
	                            By.cssSelector("a.button-collapse")));
	            user.getDriver().findElement(By.cssSelector("a.button-collapse"))
	                    .click();
	            waitForAnimations();
	            user.getWaiter().until(ExpectedConditions.elementToBeClickable(
	                    By.xpath("//ul[@id='nav-mobile']//a[text() = 'Logout']")));
	            user.getDriver()
	                    .findElement(By.xpath(
	                            "//ul[@id='nav-mobile']//a[text() = 'Logout']"))
	                    .click();
	        }

	        log.info("Logging out successful for {}", user.getClientData());

	        waitSeconds(1);
	    }

	    private boolean isClickable(String selector, BrowserUser user) {
	        try {
	            WebDriverWait wait = new WebDriverWait(user.getDriver(), 1);
	            wait.until(ExpectedConditions
	                    .elementToBeClickable(By.cssSelector(selector)));
	            return true;
	        } catch (Exception e) {
	            return false;
	        }
	    }

	    protected void openDialog(String cssSelector, BrowserUser user) {

	        log.debug("User {} opening dialog by clicking CSS '{}'",
	                user.getClientData(), cssSelector);

	        user.waitUntil(
	                ExpectedConditions
	                        .elementToBeClickable(By.cssSelector(cssSelector)),
	                "Button for opening the dialog not clickable");
	        user.getDriver().findElement(By.cssSelector(cssSelector)).click();
	        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath(
	                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")),
	                "Dialog not opened");

	        log.debug("Dialog opened for user {}", user.getClientData());
	    }

	    protected void openDialog(WebElement el, BrowserUser user) {

	        log.debug("User {} opening dialog by web element '{}'",
	                user.getClientData(), el);

	        user.waitUntil(ExpectedConditions.elementToBeClickable(el),
	                "Button for opening the dialog not clickable");
	        el.click();
	        user.waitUntil(ExpectedConditions.presenceOfElementLocated(By.xpath(
	                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")),
	                "Dialog not opened");

	        log.debug("Dialog opened for user {}", user.getClientData());
	    }

	    protected void waitForDialogClosed(String dialogId, String errorMessage,
	            BrowserUser user) {
	        log.debug("User {} waiting for dialog with id '{}' to be closed",
	                user.getClientData(), dialogId);

	        user.waitUntil(ExpectedConditions
	                .presenceOfElementLocated(By.xpath("//div[@id='" + dialogId
	                        + "' and contains(@class, 'my-modal-class') and contains(@style, 'opacity: 0') and contains(@style, 'display: none')]")),
	                "Dialog not closed. Reason: " + errorMessage);
	        user.waitUntil(
	                ExpectedConditions.invisibilityOfElementLocated(
	                        By.cssSelector(".modal.my-modal-class.open")),
	                "Dialog not closed. Reason: " + errorMessage);
	        user.waitUntil(
	                ExpectedConditions.numberOfElementsToBe(
	                        By.cssSelector(".modal-overlay"), 0),
	                "Dialog not closed. Reason: " + errorMessage);

	        log.debug("Dialog closed for user {}", user.getClientData());
	    }

	    protected void waitForAnimations() {
	        try {
	            Thread.sleep(750);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }

	    protected void waitSeconds(int seconds) {
	        try {
	            Thread.sleep(1000 * seconds);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	
}
