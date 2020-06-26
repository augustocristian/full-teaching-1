package com.fullteaching.e2e.no_elastest.utils;

import org.junit.Assert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.fullteaching.e2e.no_elastest.common.Constants.FOOTER;

public class Wait {

    public static WebDriverWait notTooMuch(WebDriver wd) {
        return new WebDriverWait(wd, 10);
    }

    public static WebDriverWait aLittle(WebDriver wd) {
        return new WebDriverWait(wd, 2);
    }

    public static void footer(WebDriver wd) {
        notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(FOOTER));
    }

    public static void waitForPageLoaded(WebDriver driver) { //13 lines
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }

}
