package com.aqacources.project.base;

import com.aqacources.project.gmail.GetEmailsFromGmail;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marina on 03.04.2019.
 */
public class RunTestRule extends TestWatcher {

    // Instance of BaseTest
    private BaseTest testClass;

    /**
     * Constructor
     *
     * @param testClass
     */
    public RunTestRule(BaseTest testClass) {
        this.testClass = testClass;
    }

    /**
     * Make all unread messages as read in GMAIL inbox
     * Make driver.quit()
     * <p>
     * <p>Overriding this method you can add some action when test is starting
     *
     * @param description
     */
    @Override
    protected void finished(Description description) {
        try {
            GetEmailsFromGmail.makeAllAsRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        testClass.getDriver().quit();
    }

    /**
     * Make screenshot and make driver.quit()
     * Also making all unread message in GMAIL inbox as read
     * <p>
     * <p>Overriding this method you can add some actions on
     * test failing<p/>
     *
     * @param e
     * @param description
     */
    @Override
    protected void failed(Throwable e, Description description) {
        // Path to directory for screenshots
        String baseDir = "src/main/resources/screenshots";
        File directory = new File(baseDir);

        // If directory doesn't exist - create it
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Get test class name and method name
        String className = description.getTestClass().getSimpleName();
        String methodName = description.getMethodName();

        // Create name of screenshot
        String screenshotName =
                baseDir
                        + "/"
                        + className
                        + "-"
                        + methodName
                        + "-"
                        + testClass.getDateTime()
                        + ".png";
        File targetFile = new File(screenshotName);

        // Create screenshot
        try {
            FileUtils.copyFile(
                    ((TakesScreenshot) testClass.getDriver()).getScreenshotAs(OutputType.FILE),
                    targetFile);
        } catch (IOException e1) {
            e1.printStackTrace();
            testClass.error(e1.getMessage());
        }

        // Write down to log file error message
        testClass.error(e.getMessage());

        try {
            GetEmailsFromGmail.makeAllAsRead();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // Driver quits on fail
        testClass.getDriver().quit();
    }

    /**
     * Make all unread messages as read in GMAIL inbox on starting
     *
     * @param description
     */
    @Override
    protected void starting(Description description) {
        try {
            GetEmailsFromGmail.makeAllAsRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
