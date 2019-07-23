package com.fullteaching.e2e.flakyexperimentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;


import com.fullteaching.e2e.no_elastest.functional.test.UserTest;


public class TestLoader {
	
	Logger logger;
	public static void main(String [] args) {
		
		
		Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  

	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("C:/temp/test/MyLogFile.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("My first log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    logger.info("Hi How r u?");  
	    
	    // Discover and filter tests
	    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
	            .request()
	            .selectors(selectPackage("io.github.bonigarcia"),
	                    selectClass(UserTest.class))
	            .filters(includeClassNamePatterns(".*Test")).build();

	    Launcher launcher = LauncherFactory.create();
	    TestPlan plan = launcher.discover(request);

	    // Executing tests
	    TestExecutionListener listener = new SummaryGeneratingListener();
	    launcher.registerTestExecutionListeners(listener);

	    launcher.execute(request, listener);
		
	}

}
