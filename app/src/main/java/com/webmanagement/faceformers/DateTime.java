package com.webmanagement.faceformers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Banpot.S on 8/22/14 AD.
 */
public class DateTime {

    /**
     * @return yyyy-MM-dd HH:mm:ss formate date as string
     */

    public static String getCurrentTimeStamp(String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCurrentTimeStamp() {
        return getCurrentTimeStamp("yyyy-MM-dd HH:mm:ss");
    }
}
