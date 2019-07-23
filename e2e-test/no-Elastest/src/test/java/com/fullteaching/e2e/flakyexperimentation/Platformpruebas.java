package com.fullteaching.e2e.flakyexperimentation;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runners.model.InitializationError;

public class Platformpruebas extends JUnitPlatform {

	public Platformpruebas(Class<?> testClass) throws InitializationError {
		
		super(testClass);

		System.out.print("JKSF");
		// TODO Auto-generated constructor stub
	}

}
