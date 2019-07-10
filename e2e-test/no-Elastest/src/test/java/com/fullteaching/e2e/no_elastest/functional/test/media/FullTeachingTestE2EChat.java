/*
 * (C) Copyright 2017 OpenVidu (http://openvidu.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.fullteaching.e2e.no_elastest.functional.test.media;

import static io.github.bonigarcia.seljup.BrowserType.CHROME;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.openqa.selenium.remote.CapabilityType.LOGGING_PREFS;
import static org.openqa.selenium.remote.DesiredCapabilities.chrome;
import static java.util.logging.Level.ALL;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.SetUp;
import com.fullteaching.e2e.no_elastest.utils.Wait;

import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.DriverCapabilities;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

/**
 * E2E tests for FullTeaching chat in a video session.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EChat extends BaseLoggedTest {

	public static final String chrome = "chrome";
	public static final String firefox = "firefox";
	static Exception ex = null;
	WebDriver driverteacher;
	WebDriver driverstudent;
	final String teacherMail = "teacher@gmail.com";
	final String teacherPass = "pass";
	final String teacherName = "Teacher Cheater";
	final String studentMail = "student1@gmail.com";
	final String studentPass = "pass";
	final String studentName = "Student Imprudent";


	public static Stream<Arguments> data() throws IOException {
		return ParameterLoader.getTestUsers();
	}

	@Test
	public void oneToOneChatInSessionChromeTest( @DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher,@DockerBrowser(type = CHROME) RemoteWebDriver rwdstudent) throws Exception {
		driverteacher=rwdteacher;


		String testName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		log.info("##### Start test: " + testName);

		// TEACHER

		driverteacher.manage().window().maximize();
		driverteacher = loginAndValidate(driverteacher,  teacherMail, teacherPass);



		Wait.aLittle(driverteacher);

		log.info("{} entering first course",user);

		Wait.notTooMuch(driverteacher).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
		driverteacher.findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
		.click();

		Wait.aLittle(driverteacher);

		log.info("{} navigating to 'Sessions' tab", user);

		Wait.notTooMuch(driverteacher).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
		driverteacher.findElement(By.cssSelector("#md-tab-label-0-1")).click();

		Wait.notTooMuch(driverteacher);

		log.info("{} getting into first session", user);

		driverteacher.findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

		Wait.aLittle(driverteacher);

		// Check connected message
		driverteacher.findElement(By.cssSelector("#fixed-icon")).click();
		checkSystemMessage("Connected", driverteacher);

		// STUDENT
		driverstudent=rwdstudent;

		driverstudent = loginAndValidate(driverstudent,  studentMail, studentPass);

		Wait.aLittle(driverstudent);

		Wait.notTooMuch(driverstudent).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
		driverstudent.findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
		.click();

		Wait.notTooMuch(driverstudent).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
		driverstudent.findElement(By.cssSelector("#md-tab-label-0-1")).click();

		Wait.aLittle(driverstudent);

		driverstudent.findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

		Wait.aLittle(driverstudent);

		driverstudent.findElement(By.cssSelector("#fixed-icon")).click();

		checkSystemMessage(studentName + " has connected", driverteacher);
		checkSystemMessage(teacherName + " has connected", driverstudent);

		// Test chat

		Wait.aLittle(driverstudent);

		String teacherMessage = "TEACHER CHAT MESSAGE";
		String studentMessage = "STUDENT CHAT MESSAGE";

		WebElement chatInputTeacher = driverteacher.findElement(By.id("message"));
		chatInputTeacher.sendKeys(teacherMessage);
		Wait.notTooMuch(driverteacher).until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
		driverteacher.findElement(By.id("send-btn")).click();

		Wait.aLittle(driverteacher);

		checkOwnMessage(teacherMessage, teacherName, driverteacher);
		checkStrangerMessage(teacherMessage, teacherName, driverstudent);

		WebElement chatInputStudent = driverstudent.findElement(By.id("message"));
		chatInputStudent.sendKeys(studentMessage);
		Wait.notTooMuch(driverstudent).until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
		driverstudent.findElement(By.id("send-btn")).click();

		Wait.aLittle(driverteacher);

		checkStrangerMessage(studentMessage, studentName, driverteacher);
		checkOwnMessage(studentMessage, studentName, driverstudent);

		Wait.aLittle(driverteacher);

		// Logout student
		driverstudent=UserUtilities.logOut(driverstudent, host);
		
		

		checkSystemMessage(studentName + " has disconnected", driverteacher);

	}

	private void checkOwnMessage(String message, String sender, WebDriver user) {
		log.info("Checking own message (\"{}\") for {}",message,sender);

		Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

		List<WebElement> messages = user.findElements(By.tagName("app-chat-line"));
		WebElement lastMessage = messages.get(messages.size() - 1);

		WebElement msgUser = lastMessage.findElement(By.cssSelector(".own-msg .message-header .user-name"));
		WebElement msgContent = lastMessage.findElement(By.cssSelector(".own-msg .message-content .user-message"));

		Wait.notTooMuch(user).until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
		Wait.notTooMuch(user).until(ExpectedConditions.textToBePresentInElement(msgContent, message));
	}

	private void checkStrangerMessage(String message, String sender, WebDriver user) {
		log.info("Checking another user's message (\"{}\") for {}", message, sender);

		Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

		List<WebElement> messages = user.findElements(By.tagName("app-chat-line"));
		WebElement lastMessage = messages.get(messages.size() - 1);

		WebElement msgUser = lastMessage.findElement(By.cssSelector(".stranger-msg .message-header .user-name"));
		WebElement msgContent = lastMessage.findElement(By.cssSelector(".stranger-msg .message-content .user-message"));

		Wait.notTooMuch(user).until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
		Wait.notTooMuch(user).until(ExpectedConditions.textToBePresentInElement(msgContent, message));
	}

	private void checkSystemMessage(String message, WebDriver user) {
		log.info("Checking system message (\"{}\") for {}", message,"NOACK");

		Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

		List<WebElement> messages = user.findElements(By.tagName("app-chat-line"));
		WebElement lastMessage = messages.get(messages.size() - 1);

		WebElement msgContent = lastMessage.findElement(By.cssSelector(".system-msg"));

		Wait.notTooMuch(user).until(ExpectedConditions.textToBePresentInElement(msgContent, message));
	}

}
