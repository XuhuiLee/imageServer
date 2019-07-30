package com.createarttechnology.imageserver.util;

import com.createarttechnology.imageserver.bean.PicBean;
import com.createarttechnology.imageserver.constants.ImageServerConstants;
import com.createarttechnology.jutil.StringUtil;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixuhui on 2018/10/10.
 */
public class InnerUtil {

    private static volatile int COUNTER = 0;

    private static Pattern PIC_LONG_NAME_PATTERN = Pattern.compile("((\\d{8})\\d{11})_(\\w{3,4})_(\\d+)_(\\d+)_(\\d+)");


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

    public static PicBean parsePicBean(String picName) {
        Matcher matcher = PIC_LONG_NAME_PATTERN.matcher(picName);
        if (!matcher.matches()) {
            return null;
        }

        PicBean bean = new PicBean();
        bean.setFileName(picName);
        bean.setTitle(matcher.group(1));
        bean.setDirName(matcher.group(2));
        bean.setType(matcher.group(3));
        bean.setWidth(StringUtil.convertInt(matcher.group(4), 0));
        bean.setHeight(StringUtil.convertInt(matcher.group(5), 0));
        bean.setSize(StringUtil.convertInt(matcher.group(6), 0));

        return bean;
    }

}
