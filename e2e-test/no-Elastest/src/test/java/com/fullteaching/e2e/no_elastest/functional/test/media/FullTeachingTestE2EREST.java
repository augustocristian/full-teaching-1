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

import static io.github.bonigarcia.seljup.BrowserType.CHROME;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
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
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;

import io.github.bonigarcia.seljup.SeleniumExtension;
import io.github.bonigarcia.seljup.DockerBrowser;

/**
 * E2E tests for FullTeaching REST CRUD operations.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching REST CRUD operations")
@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EREST extends BaseLoggedTest {

	private static String BROWSER;

	final String TEST_COURSE = "TEST_COURSE";
	final String TEST_COURSE_INFO = "TEST_COURSE_INFO";
	final String EDITED = " EDITED";

	final String TEACHER_MAIL = "teacher@gmail.com";
	final String TEACHER_PASS = "pass";
	final String TEACHER_NAME = "Teacher Cheater";

	String COURSE_NAME = TEST_COURSE;

	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;

	static Exception ex = null;

	WebDriver user;

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
	void courseRestOperations(@DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher) throws Exception {

		// Edit course
		user=rwdteacher;
		log.info("Editing course");

		COURSE_NAME = COURSE_NAME + EDITED;

		List<WebElement> l = user.findElements(By.className("course-put-icon"));
		openDialog(teacherName,l.get(l.size() - 1), user);

		Wait.notTooMuch(user).until((ExpectedConditions.elementToBeClickable(By.id(("input-put-course-name")))));
		user.findElement(By.id("input-put-course-name")).clear();
		user.findElement(By.id("input-put-course-name")).sendKeys(COURSE_NAME);
		user.findElement(By.id("submit-put-course-btn")).click();

		waitForDialogClosed(teacherName,"course-modal", "Edition of course failed", user);

		Wait.notTooMuch(user).until(
				ExpectedConditions.textToBe(
						By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), COURSE_NAME));

	}

	@Test
	void courseInfoRestOperations(@DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher) throws Exception {

		// Empty course info
		user=rwdteacher;
		enterCourseAndNavigateTab(COURSE_NAME, "info-tab-icon");
		Wait.notTooMuch(user).until(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector(".md-tab-body.md-tab-active"),
				By.cssSelector(".card-panel.warning")));

		log.info("Editing course information");

		// Edit course info
		user.findElement(By.id("edit-course-info")).click();
		user.findElement(By.className("ql-editor")).sendKeys(TEST_COURSE_INFO);
		user.findElement(By.id("send-info-btn")).click();
		waitForAnimations();

		Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector(".ql-editor p"), TEST_COURSE_INFO));

		log.info("Course information succesfully updated");

	}

	@Test
	void sessionRestOperations(@DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher) throws Exception {

		// Add new session
		user=rwdteacher;
		enterCourseAndNavigateTab(COURSE_NAME, "sessions-tab-icon");

		log.info("Adding new session");

		openDialog(teacherName,"#add-session-icon", user);

		// Find form elements
		WebElement titleField = user.findElement(By.id("input-post-title"));
		WebElement commentField = user.findElement(By.id("input-post-comment"));
		WebElement dateField = user.findElement(By.id("input-post-date"));
		WebElement timeField = user.findElement(By.id("input-post-time"));

		String title = "TEST LESSON NAME";
		String comment = "TEST LESSON COMMENT";

		// Fill input fields
		titleField.sendKeys(title);
		commentField.sendKeys(comment);

		if (BROWSER.equals("chrome")) {
			dateField.sendKeys("03-01-2018");
			timeField.sendKeys("03:10PM");
		} else if (BROWSER.equals("firefox")) {
			dateField.sendKeys("2018-03-01");
			timeField.sendKeys("15:10");
		}

		user.findElement(By.id("post-modal-btn")).click();

		waitForDialogClosed(TEACHER_NAME,"course-details-modal", "Addition of session failed", user);

		// Check fields of added session

		Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-title"), title));
		Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-description"), comment));
		Wait.notTooMuch(user).until(
				ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-datetime"), "Mar 1, 2018 - 15:10"));

		log.info("New session successfully added");

		// Edit session
		log.info("Editing session");

		openDialog(TEACHER_NAME,".edit-session-icon", user);

		// Find form elements
		titleField = user.findElement(By.id("input-put-title"));
		commentField = user.findElement(By.id("input-put-comment"));
		dateField = user.findElement(By.id("input-put-date"));
		timeField = user.findElement(By.id("input-put-time"));

		// Clear elements
		titleField.clear();
		commentField.clear();

		// Fill edited input fields
		titleField.sendKeys(title + EDITED);
		commentField.sendKeys(comment + EDITED);

		if (BROWSER.equals("chrome")) {
			dateField.sendKeys("04-02-2019");
			timeField.sendKeys("05:10AM");
		} else if (BROWSER.equals("firefox")) {
			dateField.sendKeys("2019-04-02");
			timeField.sendKeys("05:10");
		}

		user.findElement(By.id("put-modal-btn")).click();

		waitForDialogClosed(teacherName,"put-delete-modal", "Edition of session failed", user);

		// Check fields of edited session
		 Wait.notTooMuch(user).until((ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-title"), title + EDITED)));
		 Wait.notTooMuch(user).until(
				ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-description"), comment + EDITED));
		 Wait.notTooMuch(user).until(
				ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-datetime"), "Apr 2, 2019 - 05:10"));

		log.info("Session succesfully edited");

		// Delete session
		log.info("Deleting session");

		openDialog(TEACHER_NAME,".edit-session-icon", user);

		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.id(("label-delete-checkbox"))));
		user.findElement(By.id("label-delete-checkbox")).click();
		 Wait.notTooMuch(user).until((ExpectedConditions.elementToBeClickable(By.id(("delete-session-btn")))));
		user.findElement(By.id("delete-session-btn")).click();

		waitForDialogClosed(teacherName,"put-delete-modal", "Deletion of session failed", user);

		 Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("li.session-data"), 0));

		log.info("Session successfully deleted");

	}

	@Test
	void forumRestOperations(@DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher) throws Exception {

		// Add new entry to the forum
		user=rwdteacher;
		enterCourseAndNavigateTab(COURSE_NAME, "forum-tab-icon");

		log.info("Adding new entry to the forum");

		openDialog(teacherName,"#add-entry-icon", user);

		// Find form elements
		WebElement titleField = user.findElement(By.id("input-post-title"));
		WebElement commentField = user.findElement(By.id("input-post-comment"));

		String title = "TEST FORUM ENTRY";
		String comment = "TEST FORUM COMMENT";
		String entryDate = "a few seconds ago";

		// Fill input fields
		titleField.sendKeys(title);
		commentField.sendKeys(comment);

		user.findElement(By.id("post-modal-btn")).click();

		waitForDialogClosed(teacherName,"course-details-modal", "Addition of entry failed", user);

		// Check fields of new entry
		WebElement entryEl = user.findElement(By.cssSelector("li.entry-title"));

		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-title"), title));
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-author"), TEACHER_NAME));
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-date"), entryDate));

		log.info("New entry successfully added to the forum");

		log.info("Entering the new entry");

		entryEl.click();

		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(
				By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .message-itself"),
				comment));
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(
				By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .forum-comment-author"),
				TEACHER_NAME));

		// Comment reply

		log.info("Adding new replay to the entry's only comment");

		String reply = "TEST FORUM REPLY";
		openDialog(teacherName,".replay-icon", user);
		commentField = user.findElement(By.id("input-post-comment"));
		commentField.sendKeys(reply);

		user.findElement(By.id("post-modal-btn")).click();

		waitForDialogClosed(teacherName,"course-details-modal", "Addition of entry reply failed", user);

		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector(
				".comment-block > app-comment:first-child > div.comment-div div.comment-div .message-itself"),
				reply));
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector(
				".comment-block > app-comment:first-child > div.comment-div div.comment-div .forum-comment-author"),
				TEACHER_NAME));

		log.info("Replay sucessfully added");

		// Forum deactivation

		user.findElement(By.id("entries-sml-btn")).click();

		log.info("Deactivating forum");

		openDialog(teacherName,"#edit-forum-icon", user);

		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.id(("label-forum-checkbox"))));
		user.findElement(By.id("label-forum-checkbox")).click();
		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.id(("put-modal-btn"))));
		user.findElement(By.id("put-modal-btn")).click();

		waitForDialogClosed(teacherName,"put-delete-modal", "Deactivation of forum failed", user);

		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")));

		log.info("Forum successfully deactivated");

	}

	@Test
	void filesRestOperations(@DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher) throws Exception {
		user=rwdteacher;
		enterCourseAndNavigateTab(COURSE_NAME, "files-tab-icon");

		log.info("Checking that there are no files in the course");

		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")));

		log.info("Adding new file group");

		openDialog(teacherName,"#add-files-icon", user);

		String fileGroup = "TEST FILE GROUP";

		// Find form elements
		WebElement titleField = user.findElement(By.id("input-post-title"));
		titleField.sendKeys(fileGroup);

		user.findElement(By.id("post-modal-btn")).click();

		waitForDialogClosed(teacherName,"course-details-modal", "Addition of file group failed", user);

		// Check fields of new file group
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector(".file-group-title h5"), fileGroup));

		log.info("File group successfully added");

		// Edit file group
		log.info("Editing file group");

		openDialog(teacherName,"#edit-filegroup-icon", user);

		// Find form elements
		titleField = user.findElement(By.id("input-file-title"));
		titleField.clear();
		titleField.sendKeys(fileGroup + EDITED);

		user.findElement(By.id("put-modal-btn")).click();

		waitForDialogClosed(teacherName,"put-delete-modal", "Edition of file group failed", user);

		// Check fields of edited file group
		 Wait.notTooMuch(user).until(
				ExpectedConditions.textToBe(By.cssSelector("app-file-group .file-group-title h5"), fileGroup + EDITED));

		log.info("File group successfully edited");

		// Add file subgroup
		log.info("Adding new file sub-group");

		String fileSubGroup = "TEST FILE SUBGROUP";
		openDialog(teacherName,".add-subgroup-btn", user);
		titleField = user.findElement(By.id("input-post-title"));
		titleField.sendKeys(fileSubGroup);

		user.findElement(By.id("post-modal-btn")).click();

		waitForDialogClosed(teacherName,"course-details-modal", "Addition of file sub-group failed", user);

		// Check fields of new file subgroup
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .file-group-title h5"),
				fileSubGroup));

		log.info("File sub-group successfully added");

		log.info("Adding new file to sub-group");

		openDialog(teacherName,"app-file-group app-file-group .add-file-btn", user);

		WebElement fileUploader = user.findElement(By.className("input-file-uploader"));

		String fileName = "testFile.txt";

		log.info("Uploading file located on path '{}'",
				System.getProperty("user.dir") + "/src/test/resources/" + fileName);

		runJavascript(user,"arguments[0].setAttribute('style', 'display:block')", fileUploader);
		 Wait.notTooMuch(user).until(
				ExpectedConditions.presenceOfElementLocated(By.xpath(
						"//input[contains(@class, 'input-file-uploader') and contains(@style, 'display:block')]")));

		fileUploader.sendKeys(System.getProperty("user.dir") + "/src/test/resources/" + fileName);

		user.findElement(By.id("upload-all-btn")).click();

		// Wait for upload
		 Wait.notTooMuch(user).until(
				ExpectedConditions.presenceOfElementLocated(
						By.xpath("//div[contains(@class, 'determinate') and contains(@style, 'width: 100')]")));

		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.xpath("//i[contains(@class, 'icon-status-upload')]"), "done"));

		log.info("File upload successful");

		// Close dialog
		user.findElement(By.id("close-upload-modal-btn")).click();
		waitForDialogClosed(TEACHER_NAME,"course-details-modal", "Upload of file failed", user);

		// Check new uploaded file
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
				fileName));

		log.info("File succesfully added");

		// Edit file
		log.info("Editing file");

		openDialog(TEACHER_NAME,"app-file-group app-file-group .edit-file-name-icon", user);
		titleField = user.findElement(By.id("input-file-title"));
		titleField.clear();

		String editedFileName = "testFileEDITED.txt";

		titleField.sendKeys(editedFileName);
		user.findElement(By.id("put-modal-btn")).click();
		waitForDialogClosed(teacherName,"put-delete-modal", "Edition of file failed", user);

		// Check edited file name
		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
				editedFileName));

		log.info("File successfully edited");

		// Delete file group
		log.info("Deleting file-group");

		user.findElement(By.cssSelector("app-file-group .delete-filegroup-icon")).click();
		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")));

		log.info("File group successfully deleted");

	}

	@Test
	void attendersRestOperations(@DockerBrowser(type = CHROME) RemoteWebDriver rwdteacher) throws Exception {
		
		user=rwdteacher;
		enterCourseAndNavigateTab(COURSE_NAME, "attenders-tab-icon");

		log.info("Checking that there is only one attender to the course");

		 Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1));

		 Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.cssSelector(".attender-row-div .attender-name-p"), TEACHER_NAME));

		// Add attender fail
		log.info("Adding attender (should FAIL)");

		openDialog(TEACHER_NAME,"#add-attenders-icon", user);

		String attenderName = "studentFail@gmail.com";

		WebElement titleField = user.findElement(By.id("input-attender-simple"));
		titleField.sendKeys(attenderName);

		user.findElement(By.id("put-modal-btn")).click();
		waitForDialogClosed(teacherName,"put-delete-modal", "Addition of attender fail", user);

		 Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.fail")));

		 Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1));

		user.findElement(By.cssSelector("app-error-message .card-panel.fail .material-icons")).click();

		log.info("Attender addition successfully failed");

		// Add attender success
		log.info("Adding attender (should SUCCESS)");

		openDialog(teacherName,"#add-attenders-icon", user);

		attenderName = "student1@gmail.com";

		titleField = user.findElement(By.id("input-attender-simple"));
		titleField.sendKeys(attenderName);

		user.findElement(By.id("put-modal-btn")).click();
		waitForDialogClosed(teacherName,"put-delete-modal", "Addition of attender failed", user);

		  Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.correct")));

		  Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 2));

		user.findElement(By.cssSelector("app-error-message .card-panel.correct .material-icons")).click();

		log.info("Attender addition successfully finished");

		// Remove attender
		log.info("Removing attender");

		user.findElement(By.id("edit-attenders-icon")).click();
		  Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".del-attender-icon")));
		user.findElement(By.cssSelector(".del-attender-icon")).click();
		  Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1));

		log.info("Attender successfully removed");

	}

	/*** Auxiliary methods ***/

	private void loginTeacher(TestInfo info) {
		user.manage().window().maximize();
		try {
			user = loginAndValidate(user,  TEACHER_MAIL, TEACHER_PASS);
		} catch (BadUserException | ElementNotFoundException | NotLoggedException | TimeOutExeception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addCourse(String courseName) {
		log.info("Adding test course");

		int numberOfCourses = user.findElements(By.className("course-list-item")).size();

		openDialog(teacherName,"#add-course-icon", user);

		  Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.id(("input-post-course-name"))));
		user.findElement(By.id("input-post-course-name")).sendKeys(courseName);
		user.findElement(By.id("submit-post-course-btn")).click();

		waitForDialogClosed(teacherName,"course-modal", "Addition of course failed", user);

		  Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#course-list .course-list-item"),
				numberOfCourses + 1));
		  Wait.notTooMuch(user).until(
				ExpectedConditions.textToBe(
						By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), courseName));
	}

	private void deleteCourse(String courseName) {
		log.info("Deleting test course");

		List<WebElement> allCourses = user.findElements(By.className("course-list-item"));
		int numberOfCourses = allCourses.size();
		WebElement course = null;
		for (WebElement c : allCourses) {
			WebElement innerTitleSpan = c.findElement(By.cssSelector("div.course-title span"));
			if (innerTitleSpan.getText().equals(courseName)) {
				course = c;
				break;
			}
		}

		WebElement editIcon = course.findElement(By.className("course-put-icon"));
		openDialog(teacherName,editIcon, user);

		  Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.id(("label-delete-checkbox"))));
		user.findElement(By.id("label-delete-checkbox")).click();
		   Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(By.id(("delete-course-btn"))));
		user.findElement(By.id("delete-course-btn")).click();

		waitForDialogClosed(teacherName,"put-delete-course-modal", "Deletion of course failed", user);

		  Wait.notTooMuch(user).until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#course-list .course-list-item"),
				numberOfCourses - 1));
		  Wait.notTooMuch(user).until(
				ExpectedConditions.not(ExpectedConditions.textToBe(
						By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), courseName)));
	}

	private void enterCourseAndNavigateTab(String courseName, String tabId) {

		log.info("Entering course {}", courseName);

		List<WebElement> allCourses = user.findElements(By.cssSelector("#course-list .course-list-item div.course-title span"));
		WebElement courseSpan = null;
		for (WebElement c : allCourses) {
			if (c.getText().equals(courseName)) {
				courseSpan = c;
				break;
			}
		}

		courseSpan.click();

		Wait.notTooMuch(user).until(ExpectedConditions.textToBe(By.id("main-course-title"), courseName));

		log.info("Navigating to tab by clicking icon with id '{}'", tabId);

		user.findElement(By.id(tabId)).click();

		waitForAnimations();
	}

	private void deleteCourseIfExist() {
		user.get(host);
		 Wait.notTooMuch(user).until(ExpectedConditions.presenceOfElementLocated(By.id(("course-list"))));

		List<WebElement> allCourses = user.findElements(By.className("course-list-item"));
		WebElement course = null;
		for (WebElement c : allCourses) {
			WebElement innerTitleSpan = c.findElement(By.cssSelector("div.course-title span"));
			if (innerTitleSpan.getText().equals(COURSE_NAME)) {
				course = c;
				break;
			}
		}

		if (course != null) {
			this.deleteCourse(COURSE_NAME);
		}
	}
	 protected void openDialog(String iduser,WebElement el, WebDriver user) {

	        log.debug("User {} opening dialog by web element '{}'",
	                iduser, el);

	        Wait.notTooMuch(user).until(ExpectedConditions.elementToBeClickable(el));
	        el.click();
	        Wait.notTooMuch(user).until(ExpectedConditions.presenceOfElementLocated(By.xpath(
	                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")));

	        log.debug("Dialog opened for user {}", iduser);
	    }
	
	protected void openDialog(String id,String cssSelector, WebDriver user) {

        log.debug("User {} opening dialog by clicking CSS '{}'",
                id, cssSelector);

        Wait.notTooMuch(user).until(
                ExpectedConditions
                        .elementToBeClickable(By.cssSelector(cssSelector)));
        user.findElement(By.cssSelector(cssSelector)).click();
        Wait.notTooMuch(user).until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                "//div[contains(@class, 'modal-overlay') and contains(@style, 'opacity: 0.5')]")));

        log.debug("Dialog opened for user {}", id);
    }
	
	protected void waitForDialogClosed(String userid,String dialogId, String errorMessage,
            WebDriver user) {
        log.debug("User {} waiting for dialog with id '{}' to be closed",
                userid, dialogId);

        Wait.notTooMuch(user).until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//div[@id='" + dialogId
                        + "' and contains(@class, 'my-modal-class') and contains(@style, 'opacity: 0') and contains(@style, 'display: none')]"))
                );
        Wait.notTooMuch(user).until(
                ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".modal.my-modal-class.open")));
        Wait.notTooMuch(user).until(
                ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector(".modal-overlay"), 0));

        log.debug("Dialog closed for user {}",userid);
    }
    protected void waitForAnimations() {
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
	public Object runJavascript(WebDriver driv,String script, Object... args) {
		return ((JavascriptExecutor)driv).executeScript(script, args);
	}
}
