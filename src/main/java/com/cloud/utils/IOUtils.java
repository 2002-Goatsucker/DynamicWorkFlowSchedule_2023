package com.cloud.utils;

import java.util.ResourceBundle;

public class IOUtils {

    public static int readIntProperties(String file, String key){
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        return Integer.parseInt(bundle.getString(key));
    }

    public static double readDoubleProperties(String file, String key){
        ResourceBundle bundle = ResourceBundle.getBundle(file);
        return Double.parseDouble(bundle.getString(key));
    }

}
