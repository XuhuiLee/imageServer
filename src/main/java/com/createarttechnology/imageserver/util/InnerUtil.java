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

    // {timestamp}{id}_{type}_{width}_{height}_{size}
    private static Pattern PIC_LONG_NAME_PATTERN = Pattern.compile("((\\d{8})\\d{11})_(\\w{3,4})_(\\d+)_(\\d+)_(\\d+)");

    private static String getDateString() {
        return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
    }

    private static String getMillisString() {
        return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
    }

    /**
     * 写入时的dir
     */
    public static String getWriteDirPath(int type) {
        return getDirRootPath(type) + "/" + getDateString();
    }

    /**
     * 获取pic或prev的root
     */
    public static String getDirRootPath(int type) {
        switch (type) {
            case ImageServerConstants.TYPE_IMAGE:
                return ImageServerConstants.IMAGE_ROOT_PATH;
            case ImageServerConstants.TYPE_PREVIEW:
                return ImageServerConstants.PREVIEW_ROOT_PATH;
            default:
                return "/";
        }
    }

    /**
     * 临时文件名，最终文件的前缀
     */
    public static String getFilePrefix() {
        String result = String.format("%s%02d", getMillisString(), COUNTER++ % 100);
        if (COUNTER >= 100) {
            COUNTER %= 100;
        }
        return result;
    }

    /**
     * 根据文件名转成bean
     */
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

        bean.setLongPic(bean.getWidth() * 1.5d < bean.getHeight());
        bean.setGifPic("gif".equals(bean.getType()));
        bean.setSmallPic(bean.getWidth() <= ImageServerConstants.SMALL_PIC_THRESHOLD_INT || bean.getHeight() <= ImageServerConstants.SMALL_PIC_THRESHOLD_INT);

        return bean;
    }

}
