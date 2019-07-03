package com.fullteaching.e2e.no_elastest.functional.test;

import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.CourseNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.ForumNavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.NavigationUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.DOMMannager;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.fullteaching.e2e.no_elastest.utils.UserLoader;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import static com.fullteaching.e2e.no_elastest.common.Constants.*;


import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import com.fullteaching.e2e.no_elastest.common.BrowserUser;
import com.fullteaching.e2e.no_elastest.common.ChromeUser;
import com.fullteaching.e2e.no_elastest.common.FirefoxUser;

import io.github.bonigarcia.SeleniumExtension;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;



@ExtendWith(SeleniumExtension.class)
public class LoggedForumTest extends BaseLoggedTest {
	//We comment this because we instantiate it in the SetUp
	//protected static WebDriver driver;
	
	protected String courseName="Pseudoscientific course for treating the evil eye";
	
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";
	
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;

	
	private static String TEACHER_BROWSER;
	private static String STUDENT_BROWSER;
	private static String APP_URL;
	final static  Logger log = getLogger(lookup().lookupClass());
	public static Stream<Arguments> data() throws IOException {
		return ParameterLoader.getTestUsers();
	}
	
	
	
	  
	  @BeforeAll()
		static void setupAll() {
			System.setProperty("webdriver.chrome.driver",
	 	           "C:/chromedriver_win32/chromedriver.exe");
			if (System.getenv("ET_EUS_API") == null) {
				// Outside ElasTest
				ChromeDriverManager.getInstance(chrome).setup();
				FirefoxDriverManager.getInstance(firefox).setup();
				
			}

			if (System.getenv("ET_SUT_HOST") != null) {
				APP_URL = "https://" + System.getenv("ET_SUT_HOST") + ":"+PORT+"/";
			} else {
				APP_URL = System.getProperty("app.url");
				if (APP_URL == null) {
					APP_URL = "https://localhost:"+PORT+"/";
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


	
	
	
    /**
     * This test get login and navigate to the courses zone checking if there are 
     * any courses. Second and go to the Pseudo... course accessing to the forum
     *  and looks if its enable.If its enable, load all the entries and checks for 
     *  someone that have comments on it.Finally, with the two previous conditions,
     *  makes a assertequals to ensure that both are accomplisment
     
     */ 
    @ParameterizedTest
	  @MethodSource("data")
    public void forumLoadEntriesTest(String user, String password, String role)  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

		//driver = rwd;
    	driver= UserLoader.setupBrowser("chrome",role,user,100);
		
	 	String courseName = properties.getProperty("forum.test.course");

		driver = loginAndValidate(driver,  user, password);

    	try {
    		//navigate to courses.
    		if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host))) {	
    			driver = NavigationUtilities.toCoursesHome(driver);	
    		}
    		List <String> courses = CourseNavigationUtilities.getCoursesList(driver, host);
    		
    		assertTrue(courses.size()>0, "No courses in the list");
    		
    		//find course with forum activated 
    		boolean activated_forum_on_some_test=false;
    		boolean has_comments=false;
    		for (String course_name : courses) {
    			//go to each of the courses 
    			WebElement course = CourseNavigationUtilities.getCourseElement(driver, course_name);
    			course.findElement(COURSELIST_COURSETITLE).click();
    	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
    	    	
    	    	//go to forum tab to check if enabled:
    	    	//load forum
    	    	driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
    	    	if(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver, FORUM_ICON))) {
    	    		activated_forum_on_some_test = true;
    	        	//Load list of entries
    	    		List <String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);
    	    		if (entries_list.size()>0) {
    	    			
	    	        	//Go into first entry
    	    			for (String entry_name : entries_list) {
    	    				WebElement entry = ForumNavigationUtilities.getEntry(driver, entry_name);
    	    				driver = Click.element(driver, entry.findElement(FORUMENTRYLIST_ENTRYTITLE));
    	    				//Load comments
    	    				
    	        	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST));
    	        	    	List<WebElement>comments = ForumNavigationUtilities.getComments(driver);
    	    				if(comments.size()>0) {
    	    					has_comments = true;
    	    					List <WebElement> user_comments = ForumNavigationUtilities.getUserComments(driver, userName);  	    					
    	    				}//else go to next entry
    	    				driver = Click.element(driver, DOMMannager.getParent(driver, driver.findElement(BACK_TO_ENTRIESLIST_ICON)));
    	    			}
    	    		}//(else) if no entries go to next course
    	    		
    	    	}//(else) if forum no active go to next course
    	    	
    	    	driver = Click.element(driver, BACK_TO_DASHBOARD);
    		}
    		assertEquals((activated_forum_on_some_test&&has_comments), true, "There isn't any forum that can be used to test this [Or not activated or no entry lists or not comments]");
    		
    	}catch(ElementNotFoundException enfe) {
    		fail("Failed to navigate to courses forum:: "+ enfe.getClass()+ ": "+enfe.getLocalizedMessage());
    	}
    	
    	
    	
    }
    /**
     * This test get login and create an custom title and content with the current date.
     * After that, navigate to courses for access the forum section.In the forum creates
     * a new entry with the previous created title and content. Secondly, we ensure that
     * the entry was created correctly and ensures that there are only one comment that 
     * correponds with the body of that entry. 
     */ 
	@ParameterizedTest
	@MethodSource("data")
    public void forumNewEntryTest(String user, String password, String role)  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

	//	driver = rwd;
		driver= UserLoader.setupBrowser("chrome",role,user,100);
		driver = loginAndValidate(driver,  user, password);

    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());

    	int mYear = calendar.get(Calendar.YEAR);
    	int mMonth = calendar.get(Calendar.MONTH);
    	int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    	int mHour = calendar.get(Calendar.HOUR_OF_DAY);
    	int mMinute = calendar.get(Calendar.MINUTE);
    	int mSecond = calendar.get(Calendar.SECOND);
    	
    	String newEntryTitle = "New Entry Test "+ mDay+mMonth+mYear+mHour+mMinute+mSecond;
    	String newEntryContent = "This is the content written on the "+mDay+" of "+months[mMonth-1]+", " +mHour+":"+mMinute+","+mSecond ;
    	
    	try {
    		//navigate to courses.
    		if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host))) {	
    			driver = NavigationUtilities.toCoursesHome(driver);	
    		}
    		WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
    		course.findElement(COURSELIST_COURSETITLE).click();
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
	    	driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
	    	
	    	assertEquals(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver,FORUM_ICON)), true, "Forum not activated");
	    	
	    	driver = ForumNavigationUtilities.newEntry(driver, newEntryTitle, newEntryContent);
	       
	    	//Check entry... 
	    	WebElement newEntry = ForumNavigationUtilities.getEntry(driver, newEntryTitle);

	    	assertEquals(newEntry.findElement(FORUMENTRYLIST_ENTRY_USER).getText(),userName,"Incorrect user");
	    	
	    	driver = Click.element(driver, newEntry.findElement(FORUMENTRYLIST_ENTRYTITLE));
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST));
	    	WebElement entryTitleRow = driver.findElement(FORUMCOMMENTLIST_ENTRY_TITLE);
	    	
	    	assertEquals( entryTitleRow.getText().split("\n")[0], newEntryTitle,"Incorrect Entry Title");
	    	assertEquals( entryTitleRow.findElement(FORUMCOMMENTLIST_ENTRY_USER).getText(), userName, "Incorrect User for Entry");
	    	
	    	//first comment should be the inserted while creating the entry
	    	List<WebElement>comments = ForumNavigationUtilities.getComments(driver);
	    	assertFalse(comments.size()< 1, "No comments on the entry");
	    	
	    	WebElement newComment = comments.get(0);
	    	assertEquals(newComment.findElement(FORUMCOMMENTLIST_COMMENT_CONTENT).getText(),newEntryContent,"Bad content of comment");
	    	try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    	
	    	String comentario =newComment.findElement(FORUMCOMMENTLIST_COMMENT_USER).getText();

	    	assertEquals(comentario,userName,"Bad user in comment");
	    	
    	}catch(ElementNotFoundException enfe) {
    		fail("Failed to navigate to course forum:: "+ enfe.getClass()+ ": "+enfe.getLocalizedMessage());
    	}
    	
    }
    /**
     * This test get login and create an custom title and content with the current date.
     * After that, navigate to courses for access the forum section.If in the forum
     * there are not any entries create an new entry and gets into it.In the other hand
     * if there are  any created previuously entry get into the first of them. Secondly,
     * once we are into the entry, we looks for the new comment button, making a new comment
     * in this entry with the custom content(the current date and hour).Finally, we iterate 
     * over all comments looking for the comment that previously we create. 
     */ 
	@ParameterizedTest
	@MethodSource("data")
    public void forumNewCommentTest(String user, String password, String role)  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {

	//	driver = rwd;
		driver= UserLoader.setupBrowser("chrome",role,user,100);
		driver = loginAndValidate(driver,  user, password);

    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());

    	int mYear = calendar.get(Calendar.YEAR);
    	int mMonth = calendar.get(Calendar.MONTH);
    	int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    	int mHour = calendar.get(Calendar.HOUR_OF_DAY);
    	int mMinute = calendar.get(Calendar.MINUTE);
    	int mSecond = calendar.get(Calendar.SECOND);
    	
    	String newEntryTitle = "";
    	try {
	    	//check if course have any entry for comment
	    	if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host))) {	
				driver = NavigationUtilities.toCoursesHome(driver);	
			}
    	
			WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
			course.findElement(COURSELIST_COURSETITLE).click();
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
	    	driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
	    	assertEquals(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver,FORUM_ICON)), true, "Forum not activated");
	    	
	    	List <String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);
	    	WebElement entry; 
			if (entries_list.size()<=0) {//if not new entry
				newEntryTitle = "New Comment Test "+ mDay+mMonth+mYear+mHour+mMinute+mSecond;
		    	String newEntryContent = "This is the content written on the "+mDay+" of "+months[mMonth-1]+", " +mHour+":"+mMinute+","+mSecond ;
				driver = ForumNavigationUtilities.newEntry(driver, newEntryTitle, newEntryContent);
				entry = ForumNavigationUtilities.getEntry(driver, newEntryTitle);
			}
			else {
				entry = ForumNavigationUtilities.getEntry(driver, entries_list.get(0));
			}
			//go to entry 
			driver = Click.element(driver, entry.findElement(FORUMENTRYLIST_ENTRYTITLE));
			WebElement commentList = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST));
			
			//new comment
			WebElement newCommentIcon = commentList.findElement(FORUMCOMMENTLIST_NEWCOMMENT_ICON);
	    	driver = Click.element(driver, newCommentIcon);
	    	Wait.aLittle(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUM_NEWCOMMENT_MODAL));
	    	String newCommentContent = "COMMENT TEST"+ mDay+mMonth+mYear+mHour+mMinute+mSecond+". This is the comment written on the "+mDay+" of "+months[mMonth-1]+", " +mHour+":"+mMinute+","+mSecond ;
	
	    	WebElement comment_field = driver.findElement(FORUM_NEWCOMMENT_MODAL_TEXTFIELD);
	    	comment_field.sendKeys(newCommentContent);
	    	
	    	driver = Click.element(driver, FORUM_NEWCOMMENT_MODAL_POSTBUTTON);
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST));
	    	List<WebElement>comments = ForumNavigationUtilities.getComments(driver);
	    	
	    	//asserts
	    	assertEquals(comments.size()>1, true, "Comment list empty or only original comment");
	    	boolean commentFound = false;
	    	for (WebElement comment : comments) {
	    		//check if it is new comment
	    		if (comment.findElement(FORUMCOMMENTLIST_COMMENT_CONTENT).getText().equals(newCommentContent)) {
	    			commentFound = true;
	    			assertEquals(comment.findElement(FORUMCOMMENTLIST_COMMENT_USER).getText(),userName,"Bad user in comment");
	    		}
	    	}
	    	assertEquals(commentFound, true, "Comment not found");
	    	
    	}catch(ElementNotFoundException enfe) {
    		fail("Failed to navigate to course forum:: "+ enfe.getClass()+ ": "+enfe.getLocalizedMessage());
    	}

    }
    /**
     * This test get login and create like the previosly a custom content to make a comment
     * We proceed navigate to the courses forum zone, and check if there are any entries.
     * In the case that there are not entries, create a new entry and  replies to the 
     * first comment of it ( the content of it).In the other hand if there are entries
     * previously created, go to the first and replies to the same comment.After it, we check
     * that the comment was correctly published.
     * 
     */ 
	@ParameterizedTest
	@MethodSource("data")
    public void forumNewReply2CommentTest(String user, String password, String role)  throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {
	//	driver = rwd;
		driver= UserLoader.setupBrowser("chrome",role,user,100);
		
		driver = loginAndValidate(driver,  user, password);

    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(System.currentTimeMillis());

    	int mYear = calendar.get(Calendar.YEAR);
    	int mMonth = calendar.get(Calendar.MONTH);
    	int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    	int mHour = calendar.get(Calendar.HOUR_OF_DAY);
    	int mMinute = calendar.get(Calendar.MINUTE);
    	int mSecond = calendar.get(Calendar.SECOND);
    	
    	String newEntryTitle = "";
    	try {
	    	//check if course have any entry for comment
	    	if (!NavigationUtilities.amIHere(driver, COURSES_URL.replace("__HOST__", host))) {	
				driver = NavigationUtilities.toCoursesHome(driver);	
			}
    	
			WebElement course = CourseNavigationUtilities.getCourseElement(driver, courseName);
			course.findElement(COURSELIST_COURSETITLE).click();
	    	Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(By.id(TABS_DIV_ID)));
	    	driver = CourseNavigationUtilities.go2Tab(driver, FORUM_ICON);
	    	assertEquals(ForumNavigationUtilities.isForumEnabled(CourseNavigationUtilities.getTabContent(driver,FORUM_ICON)),true,"Forum not activated");
	    	
	    	List <String> entries_list = ForumNavigationUtilities.getFullEntryList(driver);
	    	WebElement entry; 
			if (entries_list.size()<=0) {//if not new entry
				newEntryTitle = "New Comment Test "+ mDay+mMonth+mYear+mHour+mMinute+mSecond;
		    	String newEntryContent = "This is the content written on the "+mDay+" of "+months[mMonth-1]+", " +mHour+":"+mMinute+","+mSecond ;
				driver = ForumNavigationUtilities.newEntry(driver, newEntryTitle, newEntryContent);
				entry = ForumNavigationUtilities.getEntry(driver, newEntryTitle);
			}
			else {
				entry = ForumNavigationUtilities.getEntry(driver, entries_list.get(0));
			}
			//go to entry 
			driver = Click.element(driver, entry.findElement(FORUMENTRYLIST_ENTRYTITLE));
			WebElement commentList = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST));
			List<WebElement>comments = ForumNavigationUtilities.getComments(driver);
			
			//go to first comment
			WebElement comment = comments.get(0);
			driver = Click.element(driver, comment.findElement(FORUMCOMMENTLIST_COMMENT_REPLY_ICON));
	    	
			String newReplyContent = "This is the reply written on the "+mDay+" of "+months[mMonth-1]+", " +mHour+":"+mMinute+","+mSecond ;

			//reply
			Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST_MODAL_NEWREPLY));
			
			WebElement textField = driver.findElement(FORUMCOMMENTLIST_MODAL_NEWREPLY_TEXTFIELD);
			textField.sendKeys(newReplyContent);
			driver = Click.element(driver, FORUM_NEWCOMMENT_MODAL_POSTBUTTON);
	
			commentList = Wait.notTooMuch(driver).until(ExpectedConditions.visibilityOfElementLocated(FORUMCOMMENTLIST));
			comments = ForumNavigationUtilities.getComments(driver);

			//getComment replies 
			List <WebElement> replies = ForumNavigationUtilities.getReplies(driver,comments.get(0)); //ESTAMOS
			
			WebElement newReply = null;
			for(WebElement reply: replies) {
				String text=reply.findElement(FORUMCOMMENTLIST_COMMENT_CONTENT).getText();
				
				if(text.equals(newReplyContent))
					newReply= reply;				
			}
			//assert reply
			assertNotNull(newReply,"Reply not found");
			boolean asserto=newReply.findElement(FORUMCOMMENTLIST_COMMENT_USER).getText().equals(userName);
	    	assertTrue(asserto,"Bad user in comment");
	    	
			//nested reply
	    	
			//assert nested reply
			
    	}catch(ElementNotFoundException enfe) {
    		fail("Failed to navigate to course forum:: "+ enfe.getClass()+ ": "+enfe.getLocalizedMessage());
    	}
    }
    
    protected  String months[] = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"};

}
