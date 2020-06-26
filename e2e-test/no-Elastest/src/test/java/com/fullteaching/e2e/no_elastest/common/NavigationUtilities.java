package com.fullteaching.e2e.no_elastest.common;

import com.fullteaching.e2e.no_elastest.common.exception.ElementNotFoundException;
import com.fullteaching.e2e.no_elastest.utils.Click;
import com.fullteaching.e2e.no_elastest.utils.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static com.fullteaching.e2e.no_elastest.common.Constants.*;


public class NavigationUtilities {

    public static boolean amIHere(WebDriver wd, String url) {//9lines
        String currentUrl = wd.getCurrentUrl().trim();
        String compareUrl = url;
        if (currentUrl.endsWith("/") && !compareUrl.trim().endsWith("/")) {
            compareUrl = compareUrl.trim() + "/";
        }
        if (!currentUrl.endsWith("/") && compareUrl.trim().endsWith("/")) {
            compareUrl = compareUrl.substring(0, compareUrl.length() - 2);
        }
        return (currentUrl.equals(compareUrl));
    }

    public static WebDriver getUrl(WebDriver wd, String url) {

        if (!amIHere(wd, url))
            wd.get(url);

        return wd;
    }

    public static WebDriver getUrlAndWaitFooter(WebDriver wd, String url) { //3lines
        wd = getUrl(wd, url);
        Wait.waitForPageLoaded(wd);
     //   Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(FOOTER));
        return wd;
    }

    public static WebDriver toCoursesHome(WebDriver wd) throws ElementNotFoundException { //3lines
        WebElement button = Wait.aLittle(wd).until(ExpectedConditions.presenceOfElementLocated(COURSES_BUTTON));
        wd = Click.element(wd, COURSES_BUTTON);
        Wait.notTooMuch(wd).until(ExpectedConditions.presenceOfElementLocated(COURSESDASHBOARD_TITLE));
        return wd;
    }

    public static WebElement getOption(List<WebElement> options, String find, FindOption type, String attribute) {
        for (WebElement option : options) {
            switch (type) {
                case CLASS:
                    if (find.equals(option.getAttribute("class")))
                        return option;
                    break;
                case TEXT:
                    if (find.equals(option.getText()))
                        return option;
                    break;
                case VALUE:
                    if (find.equals(option.getAttribute("value")))
                        return option;
                    break;
                default:
                    if (find.equals(option.getAttribute(attribute)))
                        return option;
                    break;
            }
        }
        return null;
    }


    public enum FindOption {
        CLASS, TEXT, VALUE, ATTRIBUTE
    }

}
