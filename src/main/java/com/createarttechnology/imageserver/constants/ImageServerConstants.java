package com.createarttechnology.imageserver.constants;

/**
 * Created by lixuhui on 2018/10/10.
 */
public interface ImageServerConstants {

    /**
     * 原图根目录
     */
    String IMAGE_ROOT_PATH = System.getProperty("catalina.base") + "/image";

    /**
     * 预览图根目录
     */
    String PREVIEW_ROOT_PATH = System.getProperty("catalina.base") + "/preview";

    /**
     * 图片类型
     */
    int TYPE_IMAGE = 0;
    int TYPE_PREVIEW = 1;

    /**
     * 小于这个值的算小图
     */
    int SMALL_PIC_THRESHOLD_INT = 500;

    /**
     * 缩放到这个大小
     */
    double PREVIEW_PIC_WIDTH = 500;

}
