package com.createarttechnology.imageserver.util;

import com.createarttechnology.imageserver.constants.ImageServerConstants;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Created by lixuhui on 2018/10/10.
 */
public class InnerUtil {

    private static volatile int COUNTER = 0;

    private static String getDateString() {
        return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
    }

    private static String getMillisString() {
        return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
    }

    public static String getDirPath() {
        return ImageServerConstants.ROOT_PATH + "/" + getDateString();
    }

    public static String getFileName() {
        String result = String.format("%s%02d", getMillisString(), COUNTER++);
        if (COUNTER >= 100) {
            COUNTER = 0;
        }
        return result;
    }

}
