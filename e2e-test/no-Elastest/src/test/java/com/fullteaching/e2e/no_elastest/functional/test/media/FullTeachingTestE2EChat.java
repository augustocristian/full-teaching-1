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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import java.util.List;

/**
 * E2E tests for FullTeaching chat in a video session.
 *
 * @author Pablo Fuente (pablo.fuente@urjc.es)
 */


@Resource(resID = "LoginService", replaceable = {})
@AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
@Resource(resID = "OpenVidu", replaceable = {})
@AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
@Resource(resID = "Course", replaceable = {"Configuration"})
@AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
@Disabled
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching chat")
@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EChat extends BaseLoggedTest {

    public static final String CHROME = "chrome";
    public static final String FIREFOX = "firefox";
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
    static Exception ex = null;
    private static String TEACHER_BROWSER = "chrome";
    private static String STUDENT_BROWSER = "chrome";
    final String teacherMail = "teacher@gmail.com";
    final String teacherPass = "pass";
    final String teacherName = "Teacher Cheater";
    final String studentMail = "student1@gmail.com";
    final String studentPass = "pass";
    final String studentName = "Student Imprudent";

    BrowserUser user;


    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READONLY")
    @Test
    void oneToOneChatInSessionChrome() {

        String testName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        log.info("##### Start test: " + testName);

        // TEACHER

        this.user = setupBrowser(TEACHER_BROWSER, testName, "Teacher", 30);

        this.slowLogin(user, teacherMail, teacherPass);

        waitSeconds(1);

        log.info("{} entering first course", user.getClientData());

        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
        user.getDriver().findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
                .click();

        waitSeconds(1);

        log.info("{} navigating to 'Sessions' tab", user.getClientData());

        user.getWaiter().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
        user.getDriver().findElement(By.cssSelector("#md-tab-label-0-1")).click();

        waitSeconds(1);

        log.info("{} getting into first session", user.getClientData());

        user.getDriver().findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

        waitSeconds(1);

        // Check connected message
        user.getDriver().findElement(By.cssSelector("#fixed-icon")).click();
        checkSystemMessage("Connected", user);

        // STUDENT

        BrowserUser student = setupBrowser(STUDENT_BROWSER, testName, "Student", 30);
        this.slowLogin(student, studentMail, studentPass);

        waitSeconds(1);

        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(("ul.collection li.collection-item:first-child div.course-title"))));
        student.getDriver().findElement(By.cssSelector("ul.collection li.collection-item:first-child div.course-title"))
                .click();

        student.getWaiter().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(("#md-tab-label-0-1"))));
        student.getDriver().findElement(By.cssSelector("#md-tab-label-0-1")).click();

        waitSeconds(1);

        student.getDriver().findElement(By.cssSelector("ul div:first-child li.session-data div.session-ready")).click();

        waitSeconds(1);

        student.getDriver().findElement(By.cssSelector("#fixed-icon")).click();

        checkSystemMessage(studentName + " has connected", user);
        checkSystemMessage(teacherName + " has connected", student);

        // Test chat

        waitSeconds(1);

        String teacherMessage = "TEACHER CHAT MESSAGE";
        String studentMessage = "STUDENT CHAT MESSAGE";

        WebElement chatInputTeacher = user.getDriver().findElement(By.id("message"));
        chatInputTeacher.sendKeys(teacherMessage);
        user.getWaiter().until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
        user.getDriver().findElement(By.id("send-btn")).click();

        waitSeconds(1);

        checkOwnMessage(teacherMessage, teacherName, user);
        checkStrangerMessage(teacherMessage, teacherName, student);

        WebElement chatInputStudent = student.getDriver().findElement(By.id("message"));
        chatInputStudent.sendKeys(studentMessage);
        student.getWaiter().until(ExpectedConditions.elementToBeClickable(By.id("send-btn")));
        student.getDriver().findElement(By.id("send-btn")).click();

        waitSeconds(1);

        checkStrangerMessage(studentMessage, studentName, user);
        checkOwnMessage(studentMessage, studentName, student);

        waitSeconds(2);

        // Logout student
        this.logout(student);
        student.dispose();

        checkSystemMessage(studentName + " has disconnected", user);

    }

    private void checkOwnMessage(String message, String sender, BrowserUser user) {
        log.info("Checking own message (\"{}\") for {}", message, user.getClientData());

        user.getWaiter().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

        List<WebElement> messages = user.getDriver().findElements(By.tagName("app-chat-line"));
        WebElement lastMessage = messages.get(messages.size() - 1);

        WebElement msgUser = lastMessage.findElement(By.cssSelector(".own-msg .message-header .user-name"));
        WebElement msgContent = lastMessage.findElement(By.cssSelector(".own-msg .message-content .user-message"));

        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgContent, message));
    }

    private void checkStrangerMessage(String message, String sender, BrowserUser user) {
        log.info("Checking another user's message (\"{}\") for {}", message, user.getClientData());

        user.getWaiter().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

        List<WebElement> messages = user.getDriver().findElements(By.tagName("app-chat-line"));
        WebElement lastMessage = messages.get(messages.size() - 1);

        WebElement msgUser = lastMessage.findElement(By.cssSelector(".stranger-msg .message-header .user-name"));
        WebElement msgContent = lastMessage.findElement(By.cssSelector(".stranger-msg .message-content .user-message"));

        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgUser, sender));
        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgContent, message));
    }

    private void checkSystemMessage(String message, BrowserUser user) {
        log.info("Checking system message (\"{}\") for {}", message, user.getClientData());

        user.getWaiter().until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("app-chat-line"), 0));

        List<WebElement> messages = user.getDriver().findElements(By.tagName("app-chat-line"));
        WebElement lastMessage = messages.get(messages.size() - 1);

        WebElement msgContent = lastMessage.findElement(By.cssSelector(".system-msg"));

        user.getWaiter().until(ExpectedConditions.textToBePresentInElement(msgContent, message));
    }

}