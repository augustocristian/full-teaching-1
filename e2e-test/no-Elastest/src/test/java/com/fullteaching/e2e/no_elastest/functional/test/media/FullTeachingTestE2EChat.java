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

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;

/**
 * E2E tests for FullTeaching chat in a video session.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching chat")
//@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EChat extends FullTeachingTestE2E {

	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;
	private static String APP_URL;

	static Exception ex = null;

	final String teacherMail = "teacher@gmail.com";
	final String teacherPass = "pass";
	final String teacherName = "Teacher Cheater";
	final String studentMail = "student1@gmail.com";
	final String studentPass = "pass";
	final String studentName = "Student Imprudent";
	static ChromeDriver teacher;
	//BrowserUser user;

	@BeforeAll()
	static void setupAll() {
		System.setProperty("webdriver.chrome.driver",
 	           "C:/chromedriver_win32/chromedriver.exe");
		if (System.getenv("ET_EUS_API") == null) {
			// Outside ElasTest
			/*ChromeDriverManager.getInstance().setup();
			FirefoxDriverManager.getInstance().setup();*/
			teacher=new ChromeDriver();
		}

		if (System.getenv("ET_SUT_HOST") != null) {
			APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":5001/";
		} else {
			APP_URL = System.getProperty("app.url");
			if (APP_URL == null) {
				APP_URL = "https://localhost:5001/";
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
			this.logout(teacher);
			//driver.dispose();
		} finally {
			log.info("##### Finish test: " +  info.getTestMethod().get().getName());
		}
	}

	@Test
	void oneToOneChatInSessionChrome() throws Exception {

        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        log.info("##### Start test: " + testName);

		// TEACHER

		this.teacher = setupBrowser(TEACHER_BROWSER, testName, "Teacher", 30);

		this.slowLogin(teacher, teacherMail, teacherPass);

		waitSeconds(1);
		
		//log.info("{} entering first course", user.getClientData());
		 WebDriverWait wait = new WebDriverWait(teacher, 1);
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
		teacher.findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
				.click();

		waitSeconds(1);
		
		//log.info("{} navigating to 'Sessions' tab", user.getClientData());

		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
		teacher.findElement(By.cssSelector("#md-tab-label-0-1")).click();

		waitSeconds(1);
		
		//log.info("{} getting into first session", user.getClientData());

		teacher.findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

		waitSeconds(1);

		// Check connected message
		teacher.findElement(By.cssSelector("#fixed-icon")).click();
		//checkSystemMessage("Connected", driver);

		// STUDENT

		ChromeDriver student = setupBrowser(STUDENT_BROWSER, testName, "Student", 30);
		this.slowLogin(student, studentMail, studentPass);

		waitSeconds(1);
		 WebDriverWait waitstudent = new WebDriverWait(student, 1);
		waitstudent.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
		student.findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
				.click();

		waitstudent.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
		student.findElement(By.cssSelector("#md-tab-label-0-1")).click();

		waitSeconds(1);

		student.findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

		waitSeconds(1);

		student.findElement(By.cssSelector("#fixed-icon")).click();

		checkSystemMessage(studentName + " has connected", teacher);
		checkSystemMessage(teacherName + " has connected", student);

		// Test chat

		waitSeconds(1);

		String teacherMessage = "TEACHER CHAT MESSAGE";
		String studentMessage = "STUDENT CHAT MESSAGE";

		WebElement chatInputTeacher = teacher.findElement(By.id("message"));
		chatInputTeacher.sendKeys(teacherMessage);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
		teacher.findElement(By.id("send-btn")).click();

		waitSeconds(1);

		checkOwnMessage(teacherMessage, teacherName, teacher);
		checkStrangerMessage(teacherMessage, teacherName, student);

		WebElement chatInputStudent = student.findElement(By.id("message"));
		chatInputStudent.sendKeys(studentMessage);
		waitstudent.until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
		student.findElement(By.id("send-btn")).click();

		waitSeconds(1);

		checkStrangerMessage(studentMessage, studentName, teacher);
		checkOwnMessage(studentMessage, studentName, student);

		waitSeconds(2);

		// Logout student
		this.logout(student);
	//	student.dispose();

		checkSystemMessage(studentName + " has disconnected", teacher);

	}

	private void checkOwnMessage(String message, String sender, ChromeDriver user) {
	//	log.info("Checking own message (\"{}\") for {}", message, user.getClientData());
		 WebDriverWait wait = new WebDriverWait(user, 1);
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

		List<WebElement> messages = user.findElements(By.tagName("app-chat-line"));
		WebElement lastMessage = messages.get(messages.size() - 1);

		WebElement msgUser = lastMessage.findElement(By.cssSelector(".own-msg .message-header .user-name"));
		WebElement msgContent = lastMessage.findElement(By.cssSelector(".own-msg .message-content .user-message"));

		wait.until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
		wait.until(ExpectedConditions.textToBePresentInElement(msgContent, message));
	}

	private void checkStrangerMessage(String message, String sender, ChromeDriver user) {
		//log.info("Checking another user's message (\"{}\") for {}", message, user.getClientData());
		 WebDriverWait wait = new WebDriverWait(user, 1);
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

		List<WebElement> messages = user.findElements(By.tagName("app-chat-line"));
		WebElement lastMessage = messages.get(messages.size() - 1);

		WebElement msgUser = lastMessage.findElement(By.cssSelector(".stranger-msg .message-header .user-name"));
		WebElement msgContent = lastMessage.findElement(By.cssSelector(".stranger-msg .message-content .user-message"));

		wait.until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
		wait.until(ExpectedConditions.textToBePresentInElement(msgContent, message));
	}

	private void checkSystemMessage(String message, ChromeDriver user) {
		//log.info("Checking system message (\"{}\") for {}", message, user.getClientData());
		 WebDriverWait wait = new WebDriverWait(user, 1);
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

		List<WebElement> messages = user.findElements(By.tagName("app-chat-line"));
		WebElement lastMessage = messages.get(messages.size() - 1);

		WebElement msgContent = lastMessage.findElement(By.cssSelector(".system-msg"));

		wait.until(ExpectedConditions.textToBePresentInElement(msgContent, message));
	}

}
