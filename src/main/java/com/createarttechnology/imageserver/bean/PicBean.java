package com.createarttechnology.imageserver.bean;

/**
 * Created by lixuhui on 2019/7/30.
 */
public class PicBean {
    private String fileName;
    private String dirName;
    private String title;
    private String type;
    private int width;
    private int height;
    private int size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PicBean{" +
                "fileName='" + fileName + '\'' +
                ", dirName='" + dirName + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", size=" + size +
                '}';
    }
}
