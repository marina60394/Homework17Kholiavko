package com.aqacources.project.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marina on 24.02.2019.
 */
public class YamlParser {

    /**
     * Parse yaml configuration file
     *
     * @return YamlFile that represents content from yaml file
     */
    public static YamlFile getYamlData() {
        YamlFile yamlFile = null;

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            yamlFile =
                    objectMapper.readValue(new File("src/main/resources/configuration.yaml"), YamlFile.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return yamlFile;
    }
}
