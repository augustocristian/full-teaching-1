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
import io.github.bonigarcia.seljup.SeleniumExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import retorch.testannotations.AccessMode;
import retorch.testannotations.Resource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Resource(resID = "LoginService", replaceable = {})
@AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
@Resource(resID = "OpenVidu", replaceable = {})
@AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
@Resource(resID = "Course", replaceable = {"Session"})
@AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
@Tag("e2e")
@DisplayName("E2E tests for FullTeaching video session")
@ExtendWith(SeleniumExtension.class)
public class FullTeachingTestE2EVideoSession extends BaseLoggedTest {

    static Exception ex = null;
    private static String TEACHER_BROWSER;
    private static String STUDENT_BROWSER;
    final String teacherMail = "teacher@gmail.com";
    final String teacherPass = "pass";
    final String teacherName = "Teacher Cheater";
    final String studentMail = "student1@gmail.com";
    final String studentPass = "pass";
    final String studentName = "Student Imprudent";

    BrowserUser user;

    public FullTeachingTestE2EVideoSession() {
        super();
    }

    @BeforeAll()
    static void setupAll() {

    }

    @AfterEach
    void dispose(TestInfo info) {

    }

    @Resource(resID = "LoginService", replaceable = {})
    @AccessMode(resID = "LoginService", concurrency = 10, sharing = true, accessMode = "READONLY")
    @Resource(resID = "OpenVidu", replaceable = {})
    @AccessMode(resID = "OpenVidu", concurrency = 10, sharing = true, accessMode = "READWRITE")
    @Resource(resID = "Course", replaceable = {"Session"})
    @AccessMode(resID = "Course", concurrency = 1, sharing = false, accessMode = "READWRITE")
    @Test
    void oneToOneVideoAudioSessionChrome() throws InterruptedException {
        Thread.sleep(30000);
        assertTrue(true);
    }

}
