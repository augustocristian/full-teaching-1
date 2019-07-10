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
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;

import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.seljup.DockerBrowser;


/**
 * E2E tests for FullTeaching video session.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching video session")
@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EVideoSession extends BaseLoggedTest {

	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;

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
	public void oneToOneVideoAudioSessionChromeTest( @DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher,@DockerBrowser(type = CHROME) RemoteWebDriver rwdstudent) throws Exception {
		String testName = new Object() {
		}.getClass().getEnclosingMethod().getName();

		log.info("##### Start test: " + testName);

		// TEACHER

		driverteacher=rwdteacher;

		driverteacher.manage().window().maximize();
		driverteacher = loginAndValidate(driverteacher,  teacherMail, teacherPass);

		Wait.aLittle(driverteacher);

		log.info("{} entering first course", teacherMail);

		Wait.notTooMuch(driverteacher).until(
				ExpectedConditions.presenceOfElementLocated(By.cssSelector(
						("ul.collection li.collection-item:first-child div.course-title"))));
		driverteacher.findElement(By.cssSelector(
				"ul.collection li.collection-item:first-child div.course-title"))
		.click();

		Wait.aLittle(driverteacher);

		log.info("{} navigating to 'Sessions' tab", teacherMail);

		Wait.notTooMuch(driverteacher).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("#md-tab-label-0-1"))));
		driverteacher.findElement(By.cssSelector("#md-tab-label-0-1"))
		.click();

		Wait.aLittle(driverteacher);

		log.info("{} getting into first session", teacherMail);

		driverteacher
		.findElement(By.cssSelector(
				"ul div:first-child li.session-data div.session-ready"))
		.click();

		Wait.aLittle(driverteacher);

		Wait.notTooMuch(driverteacher).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("div.participant video"))));

		Wait.aLittle(driverteacher);

		checkVideoPlaying(driverteacher,teacherMail,
				driverteacher
				.findElement(By.cssSelector(("div.participant video"))),
				"div.participant");

		// STUDENT
		driverstudent=rwdstudent;

		driverstudent = loginAndValidate(driverstudent,  studentMail, studentPass);




		Wait.aLittle(driverstudent);

		log.info("{} entering first course", studentMail);

		Wait.notTooMuch(driverstudent).until(
				ExpectedConditions.presenceOfElementLocated(By.cssSelector(
						("ul.collection li.collection-item:first-child div.course-title"))));
		driverstudent.findElement(By.cssSelector(
				"ul.collection li.collection-item:first-child div.course-title"))
		.click();

		Wait.aLittle(driverstudent);

		log.info("{} navigating to 'Courses' tab", studentMail);

		Wait.notTooMuch(driverstudent).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("#md-tab-label-0-1"))));
		driverstudent.findElement(By.cssSelector("#md-tab-label-0-1"))
		.click();

		Wait.aLittle(driverstudent);

		log.info("{} getting into first session", studentMail);

		driverstudent
		.findElement(By.cssSelector(
				"ul div:first-child li.session-data div.session-ready"))
		.click();

		Wait.aLittle(driverstudent);

		Wait.notTooMuch(driverstudent).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("div.participant video"))));

		Wait.aLittle(driverstudent);

		checkVideoPlaying(driverstudent,studentMail,
				driverstudent
				.findElement(By.cssSelector(("div.participant video"))),
				"div.participant");

		// Student asks for intervention
		Wait.notTooMuch(driverstudent).until(ExpectedConditions.elementToBeClickable(By
				.xpath("//div[@id='div-header-buttons']//i[text() = 'record_voice_over']")));

		log.info("{} asking for intervention",studentMail);

		driverstudent.findElement(By.xpath(
				"//div[@id='div-header-buttons']//i[text() = 'record_voice_over']"))
		.click();

		Wait.aLittle(driverstudent);

		// Teacher accepts intervention
		Wait.notTooMuch(driverstudent).until(ExpectedConditions.elementToBeClickable(
				By.xpath("//a[contains(@class, 'usr-btn')]")));

		log.info("{} accepts student intervention", studentMail);

		driverstudent
		.findElement(By.xpath("//a[contains(@class, 'usr-btn')]"))
		.click();

		// Check both videos for both users
		Wait.notTooMuch(driverstudent).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("div.participant-small video"))));
		// Small video of student
		checkVideoPlaying(driverstudent,studentMail,
				driverstudent.findElement(
						By.cssSelector(("div.participant-small video"))),
				"div.participant-small");
		// Main video of student
		checkVideoPlaying(driverstudent,studentMail,
				driverstudent.findElement(By.cssSelector(("div.participant video"))),
				"div.participant");

		Wait.notTooMuch(driverstudent).until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(("div.participant-small video"))));
		// Small video of teacher
		checkVideoPlaying(driverteacher,teacherMail,
				driverteacher.findElement(
						By.cssSelector(("div.participant-small video"))),
				"div.participant-small");
		// Main video of teacher
		checkVideoPlaying(driverteacher,teacherMail,
				driverteacher.findElement(By.cssSelector(("div.participant video"))),
				"div.participant");

		Wait.notTooMuch(driverstudent);

		// Teacher stops student intervention
		Wait.notTooMuch(driverteacher).until(ExpectedConditions.elementToBeClickable(
				By.xpath("//a[contains(@class, 'usr-btn')]")));

		log.info("{} canceling student intervention", studentMail);

		driverteacher
		.findElement(By.xpath("//a[contains(@class, 'usr-btn')]"))
		.click();

		// Wait until only one video
		Wait.notTooMuch(driverteacher).until(ExpectedConditions
				.not(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.cssSelector(("div.participant-small video")))));
		Wait.notTooMuch(driverstudent).until(ExpectedConditions
				.not(ExpectedConditions.presenceOfAllElementsLocatedBy(
						By.cssSelector(("div.participant-small video")))));

		Wait.notTooMuch(driverstudent);

		// Logout student
		// Logout student
		driverstudent=UserUtilities.logOut(driverstudent, host);
		assertEquals(true, true);

	}
	//@Test
	//  void dummyTest() throws Exception {

	//	assertEquals(true, true);

	//}

	/*
	 * @Test
	 * 
	 * @DisplayName("Cross-Browser test") void crossBrowserTest() throws
	 * Exception {
	 * 
	 * setupBrowser("chrome");
	 * 
	 * log.info("Cross-Browser test");
	 * 
	 * Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler()
	 * { public void uncaughtException(Thread th, Throwable ex) {
	 * System.out.println("Uncaught exception: " + ex); synchronized (lock) {
	 * OpenViduTestAppE2eTest.ex = new Exception(ex); } } };
	 * 
	 * Thread t = new Thread(() -> { BrowserUser user2 = new
	 * FirefoxUser("TestUser", 30); user2.getDriver().get(APP_URL); WebElement
	 * urlInput = user2.getDriver().findElement(By.id("openvidu-url"));
	 * urlInput.clear(); urlInput.sendKeys(OPENVIDU_URL); WebElement secretInput
	 * = user2.getDriver().findElement(By.id("openvidu-secret"));
	 * secretInput.clear(); secretInput.sendKeys(OPENVIDU_SECRET);
	 * 
	 * user2.getEventManager().startPolling();
	 * 
	 * user2.getDriver().findElement(By.id("add-user-btn")).click();
	 * user2.getDriver().findElement(By.className("join-btn")).click(); try {
	 * user2.getEventManager().waitUntilNumberOfEvent("videoPlaying", 2);
	 * Assert.assertTrue(user2.getEventManager()
	 * .assertMediaTracks(user2.getDriver().findElements(By.tagName("video")),
	 * true, true));
	 * user2.getEventManager().waitUntilNumberOfEvent("streamDestroyed", 1);
	 * user2.getDriver().findElement(By.id("remove-user-btn")).click();
	 * user2.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
	 * } catch (Exception e) { e.printStackTrace();
	 * Thread.currentThread().interrupt(); } user2.dispose(); });
	 * t.setUncaughtExceptionHandler(h); t.start();
	 * 
	 * user.getDriver().findElement(By.id("add-user-btn")).click();
	 * user.getDriver().findElement(By.className("join-btn")).click();
	 * 
	 * user.getEventManager().waitUntilNumberOfEvent("videoPlaying", 2);
	 * 
	 * try { System.out.println(getBase64Screenshot(user)); } catch (Exception
	 * e) { e.printStackTrace(); }
	 * 
	 * Assert.assertTrue(user.getEventManager().assertMediaTracks(user.getDriver
	 * (). findElements(By.tagName("video")), true, true));
	 * 
	 * user.getDriver().findElement(By.id("remove-user-btn")).click();
	 * 
	 * user.getEventManager().waitUntilNumberOfEvent("sessionDisconnected", 1);
	 * 
	 * t.join();
	 * 
	 * synchronized (lock) { if (OpenViduTestAppE2eTest.ex != null) { throw
	 * OpenViduTestAppE2eTest.ex; } } }
	 */

	private boolean checkVideoPlaying(WebDriver user,String id, WebElement videoElement,
			String containerQuerySelector) {

		log.info("{} waiting for video in container '{}' to be playing",
				id, containerQuerySelector);

		// Video element should be in 'readyState'='HAVE_ENOUGH_DATA'
		Wait.notTooMuch(user).until(ExpectedConditions.attributeToBe(videoElement,
				"readyState", "4"));

		// Video should have a srcObject (type MediaStream) with the attribute
		// 'active'
		// to true
		Assert.assertTrue((boolean) runJavascript(user,
				"return document.querySelector('" + containerQuerySelector
				+ "').getElementsByTagName('video')[0].srcObject.active"));

		// Video should trigger 'playing' event
		runJavascript(user,"document.querySelector('" + containerQuerySelector
				+ "').getElementsByTagName('video')[0].addEventListener('playing', window.MY_FUNC('"
				+ containerQuerySelector + "'));");

		Wait.notTooMuch(user).until(ExpectedConditions.attributeContains(
				By.id("video-playing-div"), "innerHTML", "VIDEO PLAYING"));

		runJavascript(user,
				"document.body.removeChild(document.getElementById('video-playing-div'))");

		return true;
	}
	public Object runJavascript(WebDriver driv,String script, Object... args) {
		return ((JavascriptExecutor)driv).executeScript(script, args);
	}


}
