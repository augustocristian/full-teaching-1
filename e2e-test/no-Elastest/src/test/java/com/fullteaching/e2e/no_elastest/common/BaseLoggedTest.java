package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.utils.SetUp;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import io.github.bonigarcia.SeleniumExtension;
//import io.github.bonigarcia.seljup.DriverCapabilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.FileInputStream;
import java.io.IOException;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;
import java.util.Date;
import java.util.Properties;
import static java.util.logging.Level.ALL;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.openqa.selenium.remote.CapabilityType.LOGGING_PREFS;
import static org.openqa.selenium.remote.DesiredCapabilities.chrome;

@ExtendWith(SeleniumExtension.class)
public class BaseLoggedTest {

	//protected common attributes
	protected static final String BROWSER_VERSION_LATEST = "latest";
	// For use another host
	//protected static final String host= SetUp.getHost();

	protected static final String host=LOCALHOST;

	protected static String userName;
	protected static String usermail;
	protected static String password;

	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";

	static Class<? extends WebDriver> chrome = ChromeDriver.class;
	static Class<? extends WebDriver> firefox = FirefoxDriver.class;

	protected static String APP_URL;
	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;

	protected BrowserUser user;
	protected final static Logger log = getLogger(lookup().lookupClass());

	

	protected static Properties properties;

	//@DriverCapabilities
	DesiredCapabilities capabilities = chrome();

	{
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(BROWSER, ALL);
		capabilities.setCapability(LOGGING_PREFS, logPrefs);
	}



	public  BaseLoggedTest() {
	


		if (System.getenv("ET_EUS_API") == null) {

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

		properties = new Properties();
		try {
			// load a properties file for reading
			properties.load(new FileInputStream("src/test/resources/inputs/test.properties"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}



	}

	protected BrowserUser setupBrowser(String browser, TestInfo testInfo,
			String userIdentifier, int secondsOfWait) {
		return this.setupBrowser(browser,
				testInfo.getTestMethod().get().getName(), userIdentifier,
				secondsOfWait);
	}

	protected BrowserUser setupBrowser(String browser, String testName,
			String userIdentifier, int secondsOfWait) {

		BrowserUser u;

		log.info("Starting browser ({})", browser);

		switch (browser) {
		case "chrome":
			u = new ChromeUser(userIdentifier, secondsOfWait, testName,
					userIdentifier);
			break;
		case "firefox":
			u = new FirefoxUser(userIdentifier, secondsOfWait, testName,
					userIdentifier);
			break;
		default:
			u = new ChromeUser(userIdentifier, secondsOfWait, testName,
					userIdentifier);
		}

		log.info("Navigating to {}", APP_URL);

		u.getDriver().get(APP_URL);

		final String GLOBAL_JS_FUNCTION = "var s = window.document.createElement('script');"
				+ "s.innerText = 'window.MY_FUNC = function(containerQuerySelector) {"
				+ "var elem = document.createElement(\"div\");"
				+ "elem.id = \"video-playing-div\";"
				+ "elem.innerText = \"VIDEO PLAYING\";"
				+ "document.body.appendChild(elem);"
				+ "console.log(\"Video check function successfully added to DOM by Selenium\")}';"
				+ "window.document.head.appendChild(s);";

		u.runJavascript(GLOBAL_JS_FUNCTION);
		u.getDriver().manage().window().maximize();
		return u;
	}


	@BeforeAll()
	static void setupAll() {

		properties = new Properties();
		try {
			// load a properties file for reading
			properties.load(new FileInputStream("src/test/resources/inputs/test.properties"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

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
		
		
		//log.info("Using URL {} to connect to openvidu-testapp", APP_URL);
	}


	@AfterEach
	void tearDown(TestInfo testInfo) throws IOException {
		String testName = testInfo.getTestMethod().get().getName();

		log.info("##### Finish test: {} - Driver {}", testName, user.getDriver());

		if (user != null) {
			//TODO EXPERIMENTACION
			//log.info("url:"+user.getDriver().getCurrentUrl()+"\nScreenshot (in Base64) at the end of the test:\n{}",
				//	SetUp.getBase64Screenshot(user.getDriver()));

			log.info("Browser console at the end of the test");
			LogEntries logEntries = user.getDriver().manage().logs().get(BROWSER);
			logEntries.forEach((entry) -> log.info("[{}] {} {}",
					new Date(entry.getTimestamp()), entry.getLevel(),
					entry.getMessage()));
			
			//this.logout(user);
			user.dispose();
		}
		//TEMPORAL SOLUTION TO MULTIPLE WINDOWS CREATION

		//driver.close();


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
		   try {
			userName = UserUtilities.getUserName(user.getDriver(), true, APP_URL);
		} catch (NotLoggedException | BadUserException | ElementNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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