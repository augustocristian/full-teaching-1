package com.fullteaching.e2e.no_elastest.functional.test.teacher;

import static io.github.bonigarcia.seljup.BrowserType.CHROME;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.fullteaching.e2e.no_elastest.common.*;
import io.github.bonigarcia.seljup.DockerBrowser;
import io.github.bonigarcia.seljup.SeleniumExtension;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.ExceptionsHelper;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;

@ExtendWith(SeleniumExtension.class)
public class CourseTeacherTest extends BaseLoggedTest {

	

	protected String courseName;
	
	private static String course_title;


    public static Stream<Arguments> data() throws IOException {
        return ParameterLoader.getTestTeachers();
    }
	
    /**
     * This tests get the login the user, go the the courses and select the default
     * course.Once the user its here, it clicks upon the different tabs(Corse info,sessions,Forum,Files
     * and attenders), checking that the navigation its possible.
     * 
     */
    @ParameterizedTest
	@MethodSource("data")
    public void teacherCourseMainTest(String user, String password, String role, @DockerBrowser(type = CHROME) RemoteWebDriver rwd) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

  //  driver = rwd;

		String courseName = properties.getProperty("forum.test.course");

		driver = loginAndValidate(driver,  user, password);
    	
    	try {
    		if(!NavigationUtilities.amIHere(driver,COURSES_URL.replace("__HOST__", host)))
        		driver = NavigationUtilities.toCoursesHome(driver);


	    	WebElement course_button = Wait.notTooMuch(driver).until(ExpectedConditions.presenceOfElementLocated(By.xpath(FIRSTCOURSE_XPATH+GOTOCOURSE_XPATH)));
	    	Click.element(driver, By.xpath(FIRSTCOURSE_XPATH+GOTOCOURSE_XPATH));
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
    	}catch(Exception e) {
    		fail("Failed to load Courses Tabs"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	//Check tabs
    	//Home tab 
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, HOME_ICON);	
    	} catch(Exception e) {
    		fail("Failed to load home tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, SESSION_ICON);	
    	} catch(Exception e) {
    		fail("Failed to load session tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
    	} catch(Exception e) {
    		fail("Failed to load forum tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, FILES_ICON);	
    	} catch(Exception e) {
    		fail("Failed to load files tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON);	
    	} catch(Exception e) {
    		fail("Failed to load attenders tab"+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    }
    
    /**
     * This tests get the login the user, go the the courses  and press the button of a 
     * new course, creating a new course(each course have a time stap that avoids
     * overlapping between different test).After that, we proceed to delete thoose courses, click
     * into the edit icon, check the box that allows it and clicking into the delete button
     * After that, we proceed to check if the course dont appears in the list. 
     * 
     */ 
    @ParameterizedTest
	@MethodSource("data")
    public void teacherCreateAndDeleteCourseTest(String user, String password, String role, @DockerBrowser(type = CHROME) RemoteWebDriver rwd) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

//    	driver = rwd;

		String courseName = properties.getProperty("forum.test.course");

		driver = loginAndValidate(driver,  user, password);
    	
    	boolean found = false;
    	try {
	    	// navigate to courses if not there
	    	if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host)))
	    		driver = NavigationUtilities.toCoursesHome(driver);
    	}catch (Exception e) {
    		fail("Failed to go to Courses "+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	try {
	    	// press new course button and wait for modal course-modal 
	    	WebElement new_course_button = Wait.notTooMuch(driver).until(ExpectedConditions.presenceOfElementLocated(NEWCOURSE_BUTTON));
	    	
	    	Click.byJS(driver,new_course_button);
    	
    	}catch (TimeoutException toe) {
    		fail("Button for new course not found");
    	}
    	
    	try {
    		Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(NEWCOURSE_MODAL));
    	}
    	catch (TimeoutException toe) {
    		fail("New course modal doesn't appear");
		}
    	
    	
    	//fill information
    	try {
	    	WebElement name_field = Wait.aLittle(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(NEWCOURSE_MODAL_NAMEFIELD_ID)));
	    	course_title = "Test Course_"+System.currentTimeMillis();
	    	name_field.sendKeys(course_title); //no duplicated courses
    	}catch (TimeoutException toe) {
    		fail("New course modal doesn't appear");
    	}
    	
    	//Save
    	try {
	    	WebElement save_button = Wait.aLittle(driver).until(ExpectedConditions.presenceOfElementLocated(By.id(NEWCOURSE_MODAL_SAVE_ID)));
	    	driver = Click.element(driver, By.id(NEWCOURSE_MODAL_SAVE_ID));
    	}catch (TimeoutException toe) {
    		fail("New course modal doesn't appear");
    	} catch (ElementNotFoundException e) {
    		fail("Button failed");
		}
    	
    	//check if the course appears now in the list
    	try {
	
	    	assertTrue(CourseNavigationUtilities.checkIfCourseExists(driver, course_title), "The course title hasn't been found in the list ¿Have been created?");
	    	
    	}catch (TimeoutException toe) {
    		fail("The courses list is not visible");
    	}

    	//DELETE
        try {
        	CourseNavigationUtilities.deleteCourse(driver, course_title);
        	
        	Thread.sleep(3000);
        	assertFalse(CourseNavigationUtilities.checkIfCourseExists(driver, course_title), "the course still exists");

		}catch(Exception e){
        	fail("there was an error while deleting the course");
		}

    	//Well done!!!


    }

    /**
     * This tests get the login the user, go the the courses  and in first place, edits the 
     * course title, change its name for EDIT_+ one timestamp to avoid test overlapping.After that, we proceed
     * to edit course details, deletes the details title, subtitle and content adding new values
     * for thoose fields.Second checks if this content was correctly added.In second place, checks if the
     * course forum is enabled/disbled, to proceed to check if allows disable/enable it.Finally
     * this test check if the current user, that is accessing to the course, is included
     * in the Attenders list( check if the user is in it)
     */ 
	@ParameterizedTest
	@MethodSource("data")
    public void teacherEditCourseValues(String user, String password, String role, @DockerBrowser(type = CHROME) RemoteWebDriver rwd) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

	//	driver = rwd;

		String courseName = properties.getProperty("forum.test.course");
      
		driver = loginAndValidate(driver,  user, password);
    	
    	try {
	    	// navigate to courses if not there
	    	if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host)))
	    		driver = NavigationUtilities.toCoursesHome(driver);
    	}catch(Exception e) {
    		fail("Failed to go to Courses "+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	// select first course (never mind which course -while application is in a test environment-)
    	// for more general testing use NavigationUtilities.newCourse but it will need some code rewriting.
    	
    	
    	//Modify name
    	try {
    		
    		WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
    		
    		String old_name = course.findElement(By.className("title")).getText();
    		String edition_name = "EDITION TEST_"+System.currentTimeMillis();
    		
    		driver = CourseNavigationUtilities.changeCourseName(driver, old_name, edition_name);
    		//check if course exists

	    	assertTrue(CourseNavigationUtilities.checkIfCourseExists(driver, edition_name, 3), "The course title hasn't been found in the list ¿Have been created?");

	    	//return to old name	    	
	    	driver = CourseNavigationUtilities.changeCourseName(driver, edition_name, old_name);
	    	assertTrue(CourseNavigationUtilities.checkIfCourseExists(driver, old_name, 3), "The course title hasn't been reset");

    	}catch (Exception e) {
    		fail("Failed to edit course name "+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	//Go to details and edit them
    	try {//*[@id="sticky-footer-div"]/main/app-dashboard/div/div[3]/div/div[1]/ul/li[1]/div/div[2]
    		    	    	
    		WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
    		course.findElement(COURSELIST_COURSETITLE).click();
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
	    	
    	}catch(Exception e) {
    		fail("Failed to load Courses Tabs "+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
  	
    	// Modify description TAB HOME
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, HOME_ICON);
    		
    		//Modify the description
    		WebElement editdescription_button = driver.findElement(EDITDESCRIPTION_BUTTON);
    		driver = Click.element(driver, editdescription_button);
    		
    		//wait for editor md editor???'
    		WebElement editdescription_desc = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className(EDITDESCRIPTION_CONTENTBOX_CLASS)));
    		
    		//text here?? /html/body/app/div/main/app-course-details/div/div[4]/md-tab-group/div[2]/div[1]/div/div[2]/p-editor/div/div[2]/div[1]
    		String old_desc = editdescription_desc.getAttribute("ng-reflect-model");
    		
    		//deletee old_desc
    		WebElement editor = driver.findElement(By.className("ql-editor"));
    		editor.sendKeys(SELECTALL);
    		editor.sendKeys(DELETE);
    		
    		//New Title
    		WebElement headerSelector = driver.findElement(By.className("ql-header"));
    		Click.element(driver, By.className("ql-header"));
    		WebElement picker_options = Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
    		WebElement option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Heading", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");
    		
    		assertNotNull(option,"Something went wrong while setting the Heading");
    		
    		driver  = Click.element(driver, option);
    		
    		//Write the new Title.
    		editor = driver.findElement(By.className("ql-editor"));
    		editor.sendKeys("New Title");
    		editor.sendKeys(NEWLINE);
    		
    		//New SubTitle
    		headerSelector = driver.findElement(By.className("ql-header"));
    		Click.element(driver, By.className("ql-header"));
    		picker_options = Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
    		option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Subheading", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");

    		assertNotNull(option, "Something went wrong while setting the Subheading");
    		
    		driver  = Click.element(driver, option);
    		
    		//Write the new SubTitle.
    		editor = driver.findElement(By.className("ql-editor"));
    		editor.sendKeys("New SubHeading");
    		editor.sendKeys(NEWLINE);
    		
    		//Content
    		headerSelector = driver.findElement(By.className("ql-header"));
    		Click.element(driver, By.className("ql-header"));
    		picker_options = Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-picker-options")));
    		option = NavigationUtilities.getOption(picker_options.findElements(By.className("ql-picker-item")), "Normal", NavigationUtilities.FindOption.ATTRIBUTE, "data-label");

    		assertNotNull(option,"Something went wrong while setting the Content");
    		
    		driver  = Click.element(driver, option);
    		
    		//Write the new Content.
    		editor = driver.findElement(By.className("ql-editor"));
    		editor.sendKeys("This is the normal content");
    		editor.sendKeys(NEWLINE);
    		////*[@id="textEditorRowButtons"]/a[2]
    		
    		
    		//preview? /html/body/app/div/main/app-course-details/div/div[4]/md-tab-group/div[2]/div[1]/div/div[2]/div/a[2]
    		//driver.findElement(By.xpath(EDITDESCRIPTION_PREVIEWBUTTON_XPATH)).click();
    		driver.findElement(By.xpath("//*[@id=\"textEditorRowButtons\"]/a[2]")).click();
    		
    		
    		WebElement preview = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")));
    		//chech heading
    		assertEquals("New Title", preview.findElement(By.tagName("h1")).getText(),"Heading not properly rendered");
    		//check subtitle
    		assertEquals("New SubHeading", preview.findElement(By.tagName("h2")).getText(),"Subheading not properly rendered");
    		//check content
    		assertEquals("This is the normal content", preview.findElement(By.tagName("p")).getText(),"Normal content not properly rendered");
    		
    		//save send-info-btn
    		driver.findElement(EDITDESCRIPTION_SAVEBUTTON).click();
    		
    		WebElement saved = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.className("ql-editor-custom")));
    		//chech heading
    		assertEquals( "New Title", saved.findElement(By.tagName("h1")).getText(),"Heading not properly rendered");
    		//check subtitle
    		assertEquals("New SubHeading", saved.findElement(By.tagName("h2")).getText(),"Subheading not properly rendered");
    		//check content
    		assertEquals( "This is the normal content", saved.findElement(By.tagName("p")).getText(),"Normal content not properly rendered");
    		
    		    		
    	} catch(Exception e) {	
    		fail("Failed to modify description:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
    						+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	// in sessions program 
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, SESSION_ICON);	
    		// new session ¡in session Tests!
    		// delete session ¡in session Tests!
    	} catch(Exception e) {	
    		fail("Failed to test session:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
    						+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	
    	// in forum enable/disable
    	try {
    		driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);	
    		
    		WebElement forum_tab_content = CourseNavigationUtilities.getTabContent(driver, FORUM_ICON);		
    		
    		//check if Forum is enabled 
    		if(ForumNavigationUtilities.isForumEnabled(forum_tab_content)) {
    			//if (enabled)
    			//check entries ¡Only check if there is entries and all the buttons are present!
    			assertNotNull(forum_tab_content.findElement(FORUM_NEWENTRY_ICON),"Add Entry not found");
    			assertNotNull(forum_tab_content.findElement(FORUM_EDITENTRY_ICON),"Add Entry not found");
    			
    			//disable
    			driver = ForumNavigationUtilities.disableForum(driver);

    			//enable
    			//clic edit
    			driver = ForumNavigationUtilities.enableForum(driver);
    		}
    		else {
    		//else
    			//enable
				driver = ForumNavigationUtilities.enableForum(driver);
    			
    			//check entries  ¡Only check if there is entries and all the buttons are present!
    			assertNotNull(forum_tab_content.findElement(FORUM_NEWENTRY_ICON),"Add Entry not found");
    			assertNotNull(forum_tab_content.findElement(FORUM_EDITENTRY_ICON),"Add Entry not found");
    			
    			//disable 
    			driver = ForumNavigationUtilities.disableForum(driver);
    		}
    		
    	} catch(Exception e) {	
    		fail("Failed to tests forum:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
    						+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	// in attenders
    	try {

			driver = CourseNavigationUtilities.go2Tab(driver, ATTENDERS_ICON);
			WebElement attenders_tab_content = CourseNavigationUtilities.getTabContent(driver, ATTENDERS_ICON);

			//is user in attenders?
			assertTrue(CourseNavigationUtilities.isUserInAttendersList(driver, userName), "User isn't in the attenders list");

			//is user highligthed?
			String main_user = CourseNavigationUtilities.getHighlightedAttender(driver);

			assertEquals(userName, main_user, "Main user and active user doesn't match");

    	} catch(Exception e) {
     		fail("Failed to tests attenders:: (File: CourseTeacherTest.java -line: "+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
     						+ e.getClass()+ ": "+e.getLocalizedMessage());
     	}
    		
    	
    	//Well done!
    }

    @Disabled
	@ParameterizedTest
	@MethodSource("data")
    public void teacherDeleteCourseTest(String user, String password, String role, @DockerBrowser(type = CHROME) RemoteWebDriver rwd) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

	//	driver = rwd;

		driver = loginAndValidate(driver,  user, password);
    	String courseName="";
    	// navigate to courses if not there
    	try {
	    	if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host)))
	    		driver = NavigationUtilities.toCoursesHome(driver);
    	}catch (Exception e) {
    		fail("Failed to go to Courses "+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	// create a course 
    	try {
    		courseName= CourseNavigationUtilities.newCourse(driver, host);
		
    	} catch (ElementNotFoundException e) {
    		fail("Failed to create course:: "+ e.getClass()+ ": "+e.getLocalizedMessage());
		}
    	// populate course
	    	// in sessions program 
				// TODO: new session
    		// in attenders
				// TODO: add attenders
    	
    	// delete course  	
    	try {
    		WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
    		
    		
    		WebElement edit_name_button = course.findElement(EDITCOURSE_BUTTON);
    		
    	    driver = Click.element(driver,edit_name_button);
    	    		
    	    //wait for edit modal
    	    WebElement edit_modal = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(EDITDELETE_MODAL));;
    	
    		//allow deletion
    		driver = Click.element(driver, ENABLECOURSE_DELETION_BUTTON);
    		
    		//delete	
    		driver = Click.withNRetries(driver, DELETECOURSE_BUTTON, 3, COURSELIST);
    		
    		assertFalse(CourseNavigationUtilities.checkIfCourseExists(driver, courseName), "The course have not been deleted");
    		
    	} catch(Exception e) {
    		fail("Failed to deletecourse:: (File:CourseTeacherTest.java - line:"+ExceptionsHelper.getFileLineInfo(e.getStackTrace(), "CourseTeacherTest.java")+") "
						+ e.getClass()+ ": "+e.getLocalizedMessage());
    	}
    	//Well done!
    }
    
  
    
}
