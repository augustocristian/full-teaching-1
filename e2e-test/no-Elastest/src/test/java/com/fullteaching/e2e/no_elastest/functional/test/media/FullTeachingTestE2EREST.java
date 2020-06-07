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

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import io.github.bonigarcia.SeleniumExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.util.List;

/**
 * E2E tests for FullTeaching REST CRUD operations.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */
//@Disabled
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching REST CRUD operations")
@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EREST extends BaseLoggedTest {

    static Exception ex = null;
    private static String BROWSER;
    final String TEST_COURSE = "TEST_COURSE";
    final String TEST_COURSE_INFO = "TEST_COURSE_INFO";
    final String EDITED = " EDITED";
    final String TEACHER_MAIL = "teacher@gmail.com";
    final String TEACHER_PASS = "pass";
    final String TEACHER_NAME = "Teacher Cheater";
    String COURSE_NAME = TEST_COURSE;
    BrowserUser userbrowser;

    public FullTeachingTestE2EREST() {
        super();
    }

    /*** ClassRule methods ***/

    @BeforeAll()
    static void setupAll() {
        BROWSER = System.getenv("BROWSER");

        if ((BROWSER == null) || (!BROWSER.equals(FIREFOX))) {
            BROWSER = CHROME;
        }

        log.info("Using URL {} to connect to openvidu-testapp", APP_URL);
    }

    @BeforeEach
    void setup(TestInfo info) {

        log.info("##### Start test: " + info.getTestMethod().get().getName());

        loginTeacher(info); // Teacher login
        addCourse(COURSE_NAME); // Add test course
    }

    @AfterEach
    void dispose(TestInfo info) {
        try {
            this.deleteCourseIfExist();
            this.logout(userbrowser);
            userbrowser.dispose();
        } finally {
            log.info("##### Finish test: " + info.getTestMethod().get().getName());
        }
    }


    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void courseRestOperations() {

        // Edit course

        log.info("Editing course");

        COURSE_NAME = COURSE_NAME + EDITED;

        List<WebElement> l = userbrowser.getDriver().findElements(By.className("course-put-icon"));
        openDialog(l.get(l.size() - 1), userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("input-put-course-name"))),
                "Input for course name not clickable");
        userbrowser.getDriver().findElement(By.id("input-put-course-name")).clear();
        userbrowser.getDriver().findElement(By.id("input-put-course-name")).sendKeys(COURSE_NAME);
        userbrowser.getDriver().findElement(By.id("submit-put-course-btn")).click();

        waitForDialogClosed("course-modal", "Edition of course failed", userbrowser);

        userbrowser.waitUntil(
                ExpectedConditions.textToBe(
                        By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), COURSE_NAME),
                "Unexpected course name");

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Information"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void courseInfoRestOperations() {

        // Empty course info
        enterCourseAndNavigateTab(COURSE_NAME, "info-tab-icon");
        userbrowser.waitUntil(ExpectedConditions.presenceOfNestedElementLocatedBy(By.cssSelector(".md-tab-body.md-tab-active"),
                By.cssSelector(".card-panel.warning")), "Course info wasn't empty");

        log.info("Editing course information");

        // Edit course info
        userbrowser.getDriver().findElement(By.id("edit-course-info")).click();
        userbrowser.getDriver().findElement(By.className("ql-editor")).sendKeys(TEST_COURSE_INFO);
        userbrowser.getDriver().findElement(By.id("send-info-btn")).click();
        waitForAnimations();

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".ql-editor p"), TEST_COURSE_INFO),
                "Unexpected course info");

        log.info("Course information succesfully updated");

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void sessionRestOperations() {

        // Add new session

        enterCourseAndNavigateTab(COURSE_NAME, "sessions-tab-icon");

        log.info("Adding new session");

        openDialog("#add-session-icon", userbrowser);

        // Find form elements
        WebElement titleField = userbrowser.getDriver().findElement(By.id("input-post-title"));
        WebElement commentField = userbrowser.getDriver().findElement(By.id("input-post-comment"));
        WebElement dateField = userbrowser.getDriver().findElement(By.id("input-post-date"));
        WebElement timeField = userbrowser.getDriver().findElement(By.id("input-post-time"));

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

        userbrowser.getDriver().findElement(By.id("post-modal-btn")).click();

        waitForDialogClosed("course-details-modal", "Addition of session failed", userbrowser);

        // Check fields of added session

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-title"), title),
                "Unexpected session title");
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-description"), comment),
                "Unexpected session description");
        userbrowser.waitUntil(
                ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-datetime"), "Mar 1, 2018 - 15:10"),
                "Unexpected session date-time");

        log.info("New session successfully added");

        // Edit session
        log.info("Editing session");

        openDialog(".edit-session-icon", userbrowser);

        // Find form elements
        titleField = userbrowser.getDriver().findElement(By.id("input-put-title"));
        commentField = userbrowser.getDriver().findElement(By.id("input-put-comment"));
        dateField = userbrowser.getDriver().findElement(By.id("input-put-date"));
        timeField = userbrowser.getDriver().findElement(By.id("input-put-time"));

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

        userbrowser.getDriver().findElement(By.id("put-modal-btn")).click();

        waitForDialogClosed("put-delete-modal", "Edition of session failed", userbrowser);

        // Check fields of edited session
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-title"), title + EDITED),
                "Unexpected session title");
        userbrowser.waitUntil(
                ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-description"), comment + EDITED),
                "Unexpected session description");
        userbrowser.waitUntil(
                ExpectedConditions.textToBe(By.cssSelector("li.session-data .session-datetime"), "Apr 2, 2019 - 05:10"),
                "Unexpected session date-time");

        log.info("Session succesfully edited");

        // Delete session
        log.info("Deleting session");

        openDialog(".edit-session-icon", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-delete-checkbox"))),
                "Checkbox for session deletion not clickable");
        userbrowser.getDriver().findElement(By.id("label-delete-checkbox")).click();
        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("delete-session-btn"))),
                "Button for session deletion not clickable");
        userbrowser.getDriver().findElement(By.id("delete-session-btn")).click();

        waitForDialogClosed("put-delete-modal", "Deletion of session failed", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.cssSelector("li.session-data"), 0),
                "Unexpected number of sessions");

        log.info("Session successfully deleted");

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void forumRestOperations() {

        // Add new entry to the forum

        enterCourseAndNavigateTab(COURSE_NAME, "forum-tab-icon");

        log.info("Adding new entry to the forum");

        openDialog("#add-entry-icon", userbrowser);

        // Find form elements
        WebElement titleField = userbrowser.getDriver().findElement(By.id("input-post-title"));
        WebElement commentField = userbrowser.getDriver().findElement(By.id("input-post-comment"));

        String title = "TEST FORUM ENTRY";
        String comment = "TEST FORUM COMMENT";
        String entryDate = "a few seconds ago";

        // Fill input fields
        titleField.sendKeys(title);
        commentField.sendKeys(comment);

        userbrowser.getDriver().findElement(By.id("post-modal-btn")).click();

        waitForDialogClosed("course-details-modal", "Addition of entry failed", userbrowser);

        // Check fields of new entry
        WebElement entryEl = userbrowser.getDriver().findElement(By.cssSelector("li.entry-title"));

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-title"), title),
                "Unexpected entry title in the forum");
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-author"), TEACHER_NAME),
                "Unexpected entry author in the forum");
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("li.entry-title .forum-entry-date"), entryDate),
                "Unexpected entry date in the forum");

        log.info("New entry successfully added to the forum");

        log.info("Entering the new entry");

        entryEl.click();

        userbrowser.waitUntil(ExpectedConditions.textToBe(
                By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .message-itself"),
                comment), "Unexpected entry title in the entry details view");
        userbrowser.waitUntil(ExpectedConditions.textToBe(
                By.cssSelector(".comment-block > app-comment:first-child > div.comment-div .forum-comment-author"),
                TEACHER_NAME), "Unexpected entry author in the entry details view");

        // Comment reply

        log.info("Adding new replay to the entry's only comment");

        String reply = "TEST FORUM REPLY";
        openDialog(".replay-icon", userbrowser);
        commentField = userbrowser.getDriver().findElement(By.id("input-post-comment"));
        commentField.sendKeys(reply);

        userbrowser.getDriver().findElement(By.id("post-modal-btn")).click();

        waitForDialogClosed("course-details-modal", "Addition of entry reply failed", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector(
                ".comment-block > app-comment:first-child > div.comment-div div.comment-div .message-itself"),
                reply), "Unexpected reply message in the entry details view");
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector(
                ".comment-block > app-comment:first-child > div.comment-div div.comment-div .forum-comment-author"),
                TEACHER_NAME), "Unexpected reply author in the entry details view");

        log.info("Replay sucessfully added");

        // Forum deactivation

        userbrowser.getDriver().findElement(By.id("entries-sml-btn")).click();

        log.info("Deactivating forum");

        openDialog("#edit-forum-icon", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-forum-checkbox"))),
                "Checkbox for forum deactivation not clickable");
        userbrowser.getDriver().findElement(By.id("label-forum-checkbox")).click();
        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("put-modal-btn"))),
                "Button for forum deactivation not clickable");
        userbrowser.getDriver().findElement(By.id("put-modal-btn")).click();

        waitForDialogClosed("put-delete-modal", "Deactivation of forum failed", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (forum deactivated) missing");

        log.info("Forum successfully deactivated");

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Files"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void filesRestOperations() {

        enterCourseAndNavigateTab(COURSE_NAME, "files-tab-icon");

        log.info("Checking that there are no files in the course");

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (course with no files) missing");

        log.info("Adding new file group");

        openDialog("#add-files-icon", userbrowser);

        String fileGroup = "TEST FILE GROUP";

        // Find form elements
        WebElement titleField = userbrowser.getDriver().findElement(By.id("input-post-title"));
        titleField.sendKeys(fileGroup);

        userbrowser.getDriver().findElement(By.id("post-modal-btn")).click();

        waitForDialogClosed("course-details-modal", "Addition of file group failed", userbrowser);

        // Check fields of new file group
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".file-group-title h5"), fileGroup),
                "Unexpected file group name");

        log.info("File group successfully added");

        // Edit file group
        log.info("Editing file group");

        openDialog("#edit-filegroup-icon", userbrowser);

        // Find form elements
        titleField = userbrowser.getDriver().findElement(By.id("input-file-title"));
        titleField.clear();
        titleField.sendKeys(fileGroup + EDITED);

        userbrowser.getDriver().findElement(By.id("put-modal-btn")).click();

        waitForDialogClosed("put-delete-modal", "Edition of file group failed", userbrowser);

        // Check fields of edited file group
        userbrowser.waitUntil(
                ExpectedConditions.textToBe(By.cssSelector("app-file-group .file-group-title h5"), fileGroup + EDITED),
                "Unexpected file group name");

        log.info("File group successfully edited");

        // Add file subgroup
        log.info("Adding new file sub-group");

        String fileSubGroup = "TEST FILE SUBGROUP";
        openDialog(".add-subgroup-btn", userbrowser);
        titleField = userbrowser.getDriver().findElement(By.id("input-post-title"));
        titleField.sendKeys(fileSubGroup);

        userbrowser.getDriver().findElement(By.id("post-modal-btn")).click();

        waitForDialogClosed("course-details-modal", "Addition of file sub-group failed", userbrowser);

        // Check fields of new file subgroup
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .file-group-title h5"),
                fileSubGroup), "Unexpected file sub-group name");

        log.info("File sub-group successfully added");

        log.info("Adding new file to sub-group");

        openDialog("app-file-group app-file-group .add-file-btn", userbrowser);

        WebElement fileUploader = userbrowser.getDriver().findElement(By.className("input-file-uploader"));

        String fileName = "testFile.txt";

        log.info("Uploading file located on path '{}'",
                System.getProperty("user.dir") + "/src/test/resources/" + fileName);

        userbrowser.runJavascript("arguments[0].setAttribute('style', 'display:block')", fileUploader);
        userbrowser.waitUntil(
                ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//input[contains(@class, 'input-file-uploader') and contains(@style, 'display:block')]")),
                "Waiting for the input file to be displayed");

        fileUploader.sendKeys(System.getProperty("user.dir") + "/src/test/resources/" + fileName);

        userbrowser.getDriver().findElement(By.id("upload-all-btn")).click();

        // Wait for upload
        userbrowser.waitUntil(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'determinate') and contains(@style, 'width: 100')]")),
                "Upload process not completed. Progress bar not filled");

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.xpath("//i[contains(@class, 'icon-status-upload')]"), "done"),
                "Upload process failed");

        log.info("File upload successful");

        // Close dialog
        userbrowser.getDriver().findElement(By.id("close-upload-modal-btn")).click();
        waitForDialogClosed("course-details-modal", "Upload of file failed", userbrowser);

        // Check new uploaded file
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
                fileName), "Unexpected uploaded file name");

        log.info("File succesfully added");

        // Edit file
        log.info("Editing file");

        openDialog("app-file-group app-file-group .edit-file-name-icon", userbrowser);
        titleField = userbrowser.getDriver().findElement(By.id("input-file-title"));
        titleField.clear();

        String editedFileName = "testFileEDITED.txt";

        titleField.sendKeys(editedFileName);
        userbrowser.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Edition of file failed", userbrowser);

        // Check edited file name
        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector("app-file-group app-file-group .chip .file-name-div"),
                editedFileName), "Unexpected uploaded file name");

        log.info("File successfully edited");

        // Delete file group
        log.info("Deleting file-group");

        userbrowser.getDriver().findElement(By.cssSelector("app-file-group .delete-filegroup-icon")).click();
        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.warning")),
                "Warning card (course with no files) missing");

        log.info("File group successfully deleted");

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Attenders"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void attendersRestOperations() {

        enterCourseAndNavigateTab(COURSE_NAME, "attenders-tab-icon");

        log.info("Checking that there is only one attender to the course");

        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.cssSelector(".attender-row-div .attender-name-p"), TEACHER_NAME),
                "Unexpected name for the attender");

        // Add attender fail
        log.info("Adding attender (should FAIL)");

        openDialog("#add-attenders-icon", userbrowser);

        String attenderName = "studentFail@gmail.com";

        WebElement titleField = userbrowser.getDriver().findElement(By.id("input-attender-simple"));
        titleField.sendKeys(attenderName);

        userbrowser.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Addition of attender fail", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.fail")),
                "Error card (attender not added to the course) missing");

        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");

        userbrowser.getDriver().findElement(By.cssSelector("app-error-message .card-panel.fail .material-icons")).click();

        log.info("Attender addition successfully failed");

        // Add attender success
        log.info("Adding attender (should SUCCESS)");

        openDialog("#add-attenders-icon", userbrowser);

        attenderName = "student1@gmail.com";

        titleField = userbrowser.getDriver().findElement(By.id("input-attender-simple"));
        titleField.sendKeys(attenderName);

        userbrowser.getDriver().findElement(By.id("put-modal-btn")).click();
        waitForDialogClosed("put-delete-modal", "Addition of attender failed", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector("app-error-message .card-panel.correct")),
                "Success card (attender properly added to the course) missing");

        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 2),
                "Unexpected number of attenders for the course");

        userbrowser.getDriver().findElement(By.cssSelector("app-error-message .card-panel.correct .material-icons")).click();

        log.info("Attender addition successfully finished");

        // Remove attender
        log.info("Removing attender");

        userbrowser.getDriver().findElement(By.id("edit-attenders-icon")).click();
        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.cssSelector(".del-attender-icon")),
                "Button for attender deletion not clickable");
        userbrowser.getDriver().findElement(By.cssSelector(".del-attender-icon")).click();
        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.className("attender-row-div"), 1),
                "Unexpected number of attenders for the course");

        log.info("Attender successfully removed");

    }

    /*** Auxiliary methods ***/

    private void loginTeacher(TestInfo info) {
        this.userbrowser = setupBrowser(BROWSER, info, "TestUser", 15);
        this.quickLogin(userbrowser, TEACHER_MAIL, TEACHER_PASS);
    }

    private void addCourse(String courseName) {
        log.info("Adding test course");

        int numberOfCourses = userbrowser.getDriver().findElements(By.className("course-list-item")).size();

        openDialog("#add-course-icon", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("input-post-course-name"))),
                "Input for course name not clickable");
        userbrowser.getDriver().findElement(By.id("input-post-course-name")).sendKeys(courseName);
        userbrowser.getDriver().findElement(By.id("submit-post-course-btn")).click();

        waitForDialogClosed("course-modal", "Addition of course failed", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#course-list .course-list-item"),
                numberOfCourses + 1), "Unexpected number of courses");
        userbrowser.waitUntil(
                ExpectedConditions.textToBe(
                        By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), courseName),
                "Unexpected name for the new course");
    }

    private void deleteCourse(String courseName) {
        log.info("Deleting test course");

        List<WebElement> allCourses = userbrowser.getDriver().findElements(By.className("course-list-item"));
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
        openDialog(editIcon, userbrowser);

        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("label-delete-checkbox"))),
                "Checkbox for course deletion not clickable");
        userbrowser.getDriver().findElement(By.id("label-delete-checkbox")).click();
        userbrowser.waitUntil(ExpectedConditions.elementToBeClickable(By.id(("delete-course-btn"))),
                "Button for course deletion not clickable");
        userbrowser.getDriver().findElement(By.id("delete-course-btn")).click();

        waitForDialogClosed("put-delete-course-modal", "Deletion of course failed", userbrowser);

        userbrowser.waitUntil(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#course-list .course-list-item"),
                numberOfCourses - 1), "Unexpected number of courses");
        userbrowser.waitUntil(
                ExpectedConditions.not(ExpectedConditions.textToBe(
                        By.cssSelector("#course-list .course-list-item:last-child div.course-title span"), courseName)),
                "Unexpected name for the last of the courses");
    }

    private void enterCourseAndNavigateTab(String courseName, String tabId) {

        log.info("Entering course {}", courseName);

        List<WebElement> allCourses = userbrowser.getDriver()
                .findElements(By.cssSelector("#course-list .course-list-item div.course-title span"));
        WebElement courseSpan = null;
        for (WebElement c : allCourses) {
            if (c.getText().equals(courseName)) {
                courseSpan = c;
                break;
            }
        }

        courseSpan.click();

        userbrowser.waitUntil(ExpectedConditions.textToBe(By.id("com.fullteaching.e2e.no_elastest.main-course-title"), courseName), "Unexpected course title");

        log.info("Navigating to tab by clicking icon with id '{}'", tabId);

        userbrowser.getDriver().findElement(By.id(tabId)).click();

        waitForAnimations();
    }

    private void deleteCourseIfExist() {
        userbrowser.getDriver().get(APP_URL);
        userbrowser.waitUntil(ExpectedConditions.presenceOfElementLocated(By.id(("course-list"))), "Course list not present");

        List<WebElement> allCourses = userbrowser.getDriver().findElements(By.className("course-list-item"));
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

}