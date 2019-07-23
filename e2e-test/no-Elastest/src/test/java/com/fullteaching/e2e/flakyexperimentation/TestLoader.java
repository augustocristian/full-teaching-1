package com.fullteaching.e2e.flakyexperimentation;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
		Runtime runtime = Runtime.getRuntime();

		Logger logger = Logger.getLogger("SevillaLog");  
		FileHandler fh;  
		PrintStream ps; 

		try {  


			// This block configure the logger with handler and formatter  
			DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String datefile=timeStampPattern.format(java.time.LocalDateTime.now());

			fh = new FileHandler("C:/logssevilla/logUserTest"+datefile+".log");  
			logger.addHandler(fh);
			PrintStream stream= new PrintStream("C:/logssevilla/logUserTest"+datefile+".log");
			System.setOut(stream);

			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  
			logger.addHandler(new StreamHandler(System.out, new SimpleFormatter()));

			// the following statement is used to log any messages  
			logger.info("My first log");  

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		logger.info(String.format("STATS \n La memoria libre es %d \n La memoria empleada es %d \n   ",runtime.freeMemory(),runtime.maxMemory()));
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
		logger.info(String.format("STATS \n La memoria libre es %d \n La memoria empleada es %d   ",runtime.freeMemory(),runtime.maxMemory()));

		//logger.info();

	}

}
