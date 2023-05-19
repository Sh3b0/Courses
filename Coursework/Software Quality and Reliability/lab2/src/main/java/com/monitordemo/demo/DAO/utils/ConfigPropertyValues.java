package com.monitordemo.demo.DAO.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author abliekassama@yahoo.com
 *
 */

public class ConfigPropertyValues {
    InputStream inputStream;

    /**
     * Get config value
     *
     * @param propertyName  config key to look for.
     *
     *@return returns the value of propertyName passed
     */
    public String getPropValues(String propertyName) {
        String propertyValue = "";
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            // get the property value
            propertyValue = prop.getProperty(propertyName);

            inputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return propertyValue;
    }
}
