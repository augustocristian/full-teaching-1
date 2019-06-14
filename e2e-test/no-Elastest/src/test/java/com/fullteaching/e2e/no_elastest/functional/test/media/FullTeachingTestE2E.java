package com.fullteaching.e2e.no_elastest.functional.test.media;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;




//@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2E {

    protected static String APP_URL;

    protected static final String CHROME = "chrome";
    protected static final String FIREFOX = "firefox";
    
    final static Logger log = getLogger(lookup().lookupClass());

    public FullTeachingTestE2E() {
        if (System.getenv("ET_EUS_API") == null) {
            // Outside ElasTest
          /*  ChromeDriverManager.getInstance().setup();
            FirefoxDriverManager.getInstance().setup();*/
        }

        if (System.getenv("ET_SUT_HOST") != null) {
            APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":5000/";
        } else {
            APP_URL = System.getProperty("app.url");
            if (APP_URL == null) {
                APP_URL = "https://localhost:5001/";
            }
        }
    }

    protected ChromeDriver setupBrowser(String browser, TestInfo testInfo,
            String userIdentifier, int secondsOfWait) {
        return new ChromeDriver();
    }

    protected ChromeDriver setupBrowser(String browser, String testName,
            String userIdentifier, int secondsOfWait) {

        ChromeDriver u= new ChromeDriver();
        u.navigate().to("https://localhost:5001");

        /*log.info("Starting browser ({})", browser);

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

        u.runJavascript(GLOBAL_JS_FUNCTION);*/

        return u;
    }

    protected void slowLogin(ChromeDriver user, String userEmail,
            String userPass) {
        this.login(user, userEmail, userPass, true);
    }

    protected void quickLogin(ChromeDriver user, String userEmail,
            String userPass) {
        this.login(user, userEmail, userPass, false);
    }

    private void login(ChromeDriver user, String userEmail, String userPass,
            boolean slow) {

        log.info("Logging in user {} with mail '{}'",
                userEmail);

        openDialog("#download-button", user);

        // Find form elements (login modal is already opened)
        WebElement userNameField = user.findElement(By.id("email"));
        WebElement userPassField = user
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

        user.findElement(By.id("log-in-btn")).click();
        WebDriverWait wait = new WebDriverWait(user, 1);
        wait.until(
                ExpectedConditions.elementToBeClickable(By.id(("course-list"))));

      //  log.info("Logging in successful for user {}", user.getClientData());
    }

    protected void logout(ChromeDriver user) {

     //   log.info("Logging out {}", user.getClientData());

        if (user.findElements(By.cssSelector("#fixed-icon"))
                .size() > 0) {
            // Get out of video session page
            if (!isClickable("#exit-icon", user)) { // Side menu not opened
                user.findElement(By.cssSelector("#fixed-icon"))
                        .click();
                waitForAnimations();
            } 
            WebDriverWait wait = new WebDriverWait(user, 1);
            wait.until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("#exit-icon")));
            user.findElement(By.cssSelector("#exit-icon")).click();
        }
        try {
            // Up bar menu
        	 WebDriverWait wait = new WebDriverWait(user, 1);
            wait.withTimeout(1000, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("#arrow-drop-down")));
            user.findElement(By.cssSelector("#arrow-drop-down"))
                    .click();
            waitForAnimations();
            wait.until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("#logout-button")));
            user.findElement(By.cssSelector("#logout-button"))
                    .click();
        } catch (TimeoutException e) {
            // Shrunk menu
        	 WebDriverWait wait = new WebDriverWait(user, 1);
            wait.withTimeout(1000, TimeUnit.MILLISECONDS)
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector("a.button-collapse")));
            user.findElement(By.cssSelector("a.button-collapse"))
                    .click();
            waitForAnimations();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//ul[@id='nav-mobile']//a[text() = 'Logout']")));
            user
                    .findElement(By.xpath(
                            "//ul[@id='nav-mobile']//a[text() = 'Logout']"))
                    .click();
        }

      //  log.info("Logging out successful for {}", user.getClientData());

        waitSeconds(1);
    }

    private boolean isClickable(String selector, ChromeDriver user) {
        try {
            WebDriverWait wait = new WebDriverWait(user, 1);
            wait.until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector(selector)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void openDialog(String cssSelector, ChromeDriver user) {

        log.debug("User {} opening dialog by clicking CSS '{}'",
                user, cssSelector);
        WebDriverWait wait = new WebDriverWait(user, 3);
        wait.until(ExpectedConditions
                        .elementToBeClickable(By.cssSelector(cssSelector)));
       user.findElement(By.cssSelector(cssSelector)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")));

    //    log.debug("Dialog opened for user {}", user.getClientData());
    }

    protected void openDialog(WebElement el, ChromeDriver user) {

      //  log.debug("User {} opening dialog by web element '{}'",
        //        user.getClientData(), el);
    	 WebDriverWait wait = new WebDriverWait(user, 1);
        wait.until(ExpectedConditions.elementToBeClickable(el)
               );
        el.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")));

      //  log.debug("Dialog opened for user {}", user.getClientData());
    }

    protected void waitForDialogClosed(String dialogId, String errorMessage,
            ChromeDriver user) {
    /*    log.debug("User {} waiting for dialog with id '{}' to be closed",
                user.getClientData(), dialogId);
*/
    	 WebDriverWait wait = new WebDriverWait(user, 1);
        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//div[@id='" + dialogId
                        + "' and contains(@class, 'my-modal-class') and contains(@style, 'opacity: 0') and contains(@style, 'display: none')]")));
        wait.until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".modal.my-modal-class.open"))
                );
        wait.until(
                ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector(".modal-overlay"), 0)
              );

    //    log.debug("Dialog closed for user {}", user.getClientData());
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
