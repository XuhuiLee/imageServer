package com.createarttechnology.imageserver.bean;

/**
 * Created by lixuhui on 2019/7/30.
 */
public class WaterfallBean {
    private String title;
    private String src;
    private int height;
    private int width;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "WaterfallResp{" +
                "title='" + title + '\'' +
                ", src='" + src + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
