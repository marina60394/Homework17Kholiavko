package com.aqacources.project.tests;

import com.aqacources.project.base.BaseTest;
import com.aqacources.project.pages.HomePage;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Marina on 03.04.2019.
 */
public class RegisterNewUser extends BaseTest {

    /**
     * Register new user
     * Confirm registration
     */
    @Test
    public void testRegisterNewUser() {

        // Open site
        HomePage homePage = openSite();
        log("Opened site");

        // Click 'Register new user'
        homePage.clickRegisterNewUser();
        log("Clicked 'Register new user'");

        // Register new user
        try {
            homePage.registerNewUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Registered new user");

        // Verify that registration is ok
        homePage.verifyRegistration();
        log("Verified registration");
    }

}
