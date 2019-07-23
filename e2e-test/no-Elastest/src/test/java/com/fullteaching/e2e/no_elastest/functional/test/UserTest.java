package com.fullteaching.e2e.no_elastest.functional.test;


import com.fullteaching.e2e.no_elastest.common.BaseLoggedTest;
import com.fullteaching.e2e.no_elastest.common.UserUtilities;
import com.fullteaching.e2e.no_elastest.common.exception.BadUserException;
import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.common.exception.NotLoggedException;
import com.fullteaching.e2e.no_elastest.common.exception.TimeOutExeception;
import com.fullteaching.e2e.no_elastest.utils.ParameterLoader;
import com.sun.management.OperatingSystemMXBean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.stream.Stream;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.SeleniumExtension;




@ExtendWith(SeleniumExtension.class)
public class UserTest extends BaseLoggedTest {

	
	public static final String CHROME = "chrome";
	public static final String FIREFOX = "firefox";

	WebDriver driver;
	static Logger logger ;
	
    static Class<? extends WebDriver> chrome = ChromeDriver.class;
    static Class<? extends WebDriver> firefox = FirefoxDriver.class;
	

	

	public static Stream<Arguments> data() throws IOException {
		SetUpLogs();
        return ParameterLoader.getTestUsers();
    }
	

	public static void SetUpLogs() {
		
		logger = Logger.getLogger("LoggerBasico");  
		FileHandler fh;  
		PrintStream ps; 

		try {  


			// This block configure the logger with handler and formatter  
			DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String datefile=timeStampPattern.format(java.time.LocalDateTime.now());

			fh = new FileHandler("C:/logssevilla/logUserTest"+datefile+".csv");  
			logger.addHandler(fh);
			

			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
			

			// the following statement is used to log any messages  
			logger.info("My first log");  
			}catch(Exception e) {
				
			}

	}

public void addCsVTouple (Date datetime, long memoryused,long memoryavalible) {
	//Medir el CPU 
	 com.sun.management.OperatingSystemMXBean operatingSystemMXBean = 
	         (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	    int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
	    long prevUpTime = runtimeMXBean.getUptime();
	    long prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
	    double cpuUsage;
	    try 
	    {
	        Thread.sleep(500);
	    } 
	    catch (Exception ignored) { }

	    operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	    long upTime = runtimeMXBean.getUptime();
	    long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
	    long elapsedCpu = processCpuTime - prevProcessCpuTime;
	    long elapsedTime = upTime - prevUpTime;

	    cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
	    System.out.println("Java CPU: " + cpuUsage);
	//Uso de CPU
	String line= String.format(datetime.toGMTString()+",%d,%d,%f \n", memoryused,memoryavalible,cpuUsage);
	
	logger.log(Level.INFO,line);
}
	
    /**
     * This test is a simple logging ackenoledgment, that checks if the current logged user
     * was logged correctly
     */ 

	@ParameterizedTest
	@MethodSource("data")
	public void loginTest(String usermail, String password, String role) throws ElementNotFoundException, BadUserException, NotLoggedException, TimeOutExeception {
		Runtime runtime = Runtime.getRuntime();
		
		addCsVTouple(Calendar.getInstance().getTime(), runtime.maxMemory(), runtime.freeMemory());
		user= setupBrowser("chrome",role,usermail,100);
		driver=user.getDriver();
		try {
			this.slowLogin(user, usermail, password);
		
			driver = UserUtilities.checkLogin(driver, usermail);

			assertTrue(true, "not logged");

		} catch (NotLoggedException | BadUserException e) {
				
			e.printStackTrace();
			fail("Not logged");
			
		} catch (ElementNotFoundException e) {
			
			e.printStackTrace();
			fail(e.getLocalizedMessage());
			
		} 
		
		try {
			driver = UserUtilities.logOut(driver,host);
			
			driver = UserUtilities.checkLogOut(driver);
			
		} catch (ElementNotFoundException enfe) {
			fail("Still logged");
			
		} catch (NotLoggedException e) {
			assertTrue(true, "Not logged");
		}
			
		assertTrue(true);

		
		addCsVTouple(Calendar.getInstance().getTime(), runtime.maxMemory(), runtime.freeMemory());
	}
	
	

	
	
}
