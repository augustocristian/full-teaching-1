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

package com.fullteaching.e2e.no_elastest.common;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

public class EdgeUser extends BrowserUser {

	public EdgeUser(String userName, int timeOfWaitInSeconds, String testName, String userIdentifier) {
		super(userName, timeOfWaitInSeconds);
		

		
		String eusApiURL = System.getenv("ET_EUS_API");
	
		DesiredCapabilities capabilities = DesiredCapabilities.edge();
		capabilities.setCapability("IsJavaScriptEnabled", true);
		capabilities.setCapability("acceptInsecureCerts", true);

		if(eusApiURL == null) {
			this.driver = new EdgeDriver(capabilities);
		} else {
			try {
				capabilities.setCapability("testName", testName + "_" + userIdentifier);
				RemoteWebDriver remote = new RemoteWebDriver(new URL(eusApiURL),  capabilities);
				remote.setFileDetector(new LocalFileDetector());
				this.driver = remote;
			} catch (MalformedURLException e) {
				throw new RuntimeException("Exception creaing eusApiURL",e);
			}
		}
		
		this.driver.manage().timeouts().setScriptTimeout(this.timeOfWaitInSeconds, TimeUnit.SECONDS);
		
		this.configureDriver();
	}

}