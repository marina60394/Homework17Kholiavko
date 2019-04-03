package com.aqacources.project.pages;

import com.aqacources.project.base.BaseTest;
import org.openqa.selenium.support.PageFactory;

/**
 * Created by Marina on 03.04.2019.
 */
public class AbstractPage {

    // Instances of BaseTest
    protected BaseTest testClass;

    /**
     * Constructor
     *
     * @param testClass
     */
    public AbstractPage(BaseTest testClass) {
        this.testClass = testClass;
        PageFactory.initElements(testClass.getDriver(), this);
    }
}