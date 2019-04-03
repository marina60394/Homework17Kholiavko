package com.aqacources.project.utils;

import java.util.Map;

/**
 * Created by Marina on 24.02.2019.
 */
public class YamlFile {

    // Values from yaml file
    private String url;
    private String email;
    private String password;
    private String name;

    /**
     * Get URL from yaml file
     *
     * @return String represents URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get email from yaml file
     *
     * @return String represents email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get password from yaml file
     *
     * @return String represents password
     */
    public String getPassword() {
        return password;
    }


    /**
     * Get name from yaml file
     *
     * @return String represents name
     */
    public String getName() {
        return name;
    }
}