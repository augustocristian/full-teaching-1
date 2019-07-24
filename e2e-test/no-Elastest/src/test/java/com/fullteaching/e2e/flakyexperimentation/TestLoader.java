package com.fullteaching.e2e.flakyexperimentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;
import java.util.logging.*;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
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
		String configurations= args[0];
		FileWriter csvWriter = null;
		StringBuilder output= new StringBuilder();


		// This block configure the logger with handler and formatter  
		DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String datefile=timeStampPattern.format(java.time.LocalDateTime.now());
	
		String filename="C:/logssevilla/salidaconfigurations.csv";


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
		TestExecutionListener listenerfallos = new TestExecutionListener() {
		};
		launcher.registerTestExecutionListeners(listener);
		long startTime = System.nanoTime();
		launcher.execute(request, listener);
		long endTime = System.nanoTime();

		
		//logger.info(String.format("STATS \n La memoria libre es %d \n La memoria empleada es %d   ",runtime.freeMemory(),runtime.maxMemory()));

	//	logger.info(String.format("Aciertos: %d, Fallos : %d ",((SummaryGeneratingListener)listener).getSummary().getTestsSucceededCount(),((SummaryGeneratingListener)listener).getSummary().getTestsFailedCount()));
		String [] splittedconf=configurations.split("&");
		int nfallos=0;
		
		

		//get the current date
		Calendar cal = Calendar. getInstance();
		Date date=cal. getTime();
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

		if(((SummaryGeneratingListener)listener).getSummary().getTestsFailedCount()>0)
			nfallos=1;
		output.append(String.format("%s;%s;%s;%d;%d;%s \n",splittedconf[0],splittedconf[1],
				splittedconf[2],nfallos,(endTime-startTime)/ 1000000,dateFormat. format(date)));

		try {
			csvWriter = new FileWriter(filename,true);
			csvWriter.append(output.toString());
			 csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//logger.info();

		
		
		
	}
	

}
