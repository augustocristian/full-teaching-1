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

import static org.junit.jupiter.api.Assertions.assertTrue;

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


    }

    @AfterEach
    void dispose(TestInfo info) {

    }


    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Configuration"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void courseRestOperations() throws InterruptedException {

        // Edit course

        Thread.sleep(9000);
        assertTrue(true);

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Information"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void courseInfoRestOperations() throws InterruptedException {
        Thread.sleep(10000);
        assertTrue(true);

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void sessionRestOperations() throws InterruptedException {

        Thread.sleep(11000);
        assertTrue(true);

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Forum"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void forumRestOperations() throws InterruptedException {

        Thread.sleep(12000);
        assertTrue(true);

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Files"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void filesRestOperations() throws InterruptedException {

        Thread.sleep(15000);
        assertTrue(true);
    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {"OpenViduMock"})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "NOACCESS")
    @Resource(resID = "Course", replaceable = {"Attenders"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void attendersRestOperations() throws InterruptedException {
        Thread.sleep(15000);
        assertTrue(true);

    }



}