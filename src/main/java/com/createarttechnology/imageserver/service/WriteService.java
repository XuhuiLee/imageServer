package com.createarttechnology.imageserver.service;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.common.ErrorInfo;
import com.createarttechnology.imageserver.bean.PicBean;
import com.createarttechnology.imageserver.constants.ImageServerConstants;
import com.createarttechnology.imageserver.util.InnerUtil;
import com.createarttechnology.logger.Logger;
import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.bmp.BMPImageReaderSpi;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import com.sun.imageio.plugins.png.PNGImageReader;
import com.sun.imageio.plugins.png.PNGImageReaderSpi;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.security.InvalidParameterException;

/**
 * Created by lixuhui on 2018/10/10.
 */
@Service
public class WriteService {

    private static final Logger logger = Logger.getLogger(WriteService.class);

    /**
     * 初始化目录
     */
    @PostConstruct
    public void init() {
        File imageRootPath = new File(ImageServerConstants.IMAGE_ROOT_PATH);
        if (!imageRootPath.exists()) {
            imageRootPath.mkdir();
        }
        File previewRootPath = new File(ImageServerConstants.PREVIEW_ROOT_PATH);
        if (!previewRootPath.exists()) {
            previewRootPath.mkdir();
        }
    }

    /**
     * 根据InputStream保存图片
     */
    public BaseResp<String> savePicFile(InputStream inputStream, int type) throws Exception {
        if (inputStream == null) {
            return new BaseResp<>(ErrorInfo.INVALID_PARAMS);
        }
        try {
            String picFileName = writeFile(inputStream, type);
            logger.info("write finished, picFileName={}", picFileName);
            return new BaseResp<String>(ErrorInfo.SUCCESS).setData(picFileName);
        } catch (Exception e) {
            logger.error("write file error, e:", e);
            throw e;
        } finally {
            inputStream.close();
        }
    }

    /**
     * 将图片缩小，保存到预览目录
     */
    public synchronized boolean scaleFile(String dirName, String fileName) {
        File imgFile = new File(InnerUtil.getDirRootPath(ImageServerConstants.TYPE_IMAGE) + "/" + dirName + "/" + fileName);
        if (!imgFile.exists()) {
            // 图片不存在
            return false;
        }

        File prevFile = new File(InnerUtil.getDirRootPath(ImageServerConstants.TYPE_PREVIEW) + "/" + dirName + "/" + fileName);
        if (prevFile.exists()) {
            // 已缩过
            return true;
        }

        PicBean picBean = InnerUtil.parsePicBean(fileName);
        if (picBean == null) {
            return false;
        }

        File dir = new File(InnerUtil.getDirRootPath(ImageServerConstants.TYPE_PREVIEW) + "/" + dirName);
        if (!dir.exists()) {
            dir.mkdir();
        }

        // 长图、gif、小图先不缩小了
        if (picBean.isLongPic() || picBean.isGifPic() || picBean.isSmallPic()) {
            try {
                copyFile(imgFile, prevFile);
                return true;
            } catch (IOException e) {
                logger.error("copy error, e:", e);
                return false;
            }
        }

        try {
            /*
            这段是抄来的
            copy from https://blog.csdn.net/chwshuang/article/details/64923333
             */
            BufferedImage bufferedImage = ImageIO.read(imgFile);
            //计算压缩比例
            double resizeTimes = ImageServerConstants.PREVIEW_PIC_WIDTH / picBean.getWidth();

            /* 调整后的图片的宽度和高度 - 按照压缩比例计算出新的宽度和高度 */
            int toWidth = (int) (picBean.getWidth() * resizeTimes);
            int toHeight = (int) (picBean.getWidth() * resizeTimes);

            /* 新生成结果图片 */
            BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
            result.getGraphics().drawImage(bufferedImage.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);

            ImageIO.write(result, picBean.getType(), new File(InnerUtil.getDirRootPath(ImageServerConstants.TYPE_PREVIEW) + "/" + dirName + "/" + fileName));
            return true;
        } catch (IOException e) {
            logger.error("scale error, e:", e);
            return false;
        }
    }

    private void copyFile(File from, File to) throws IOException {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inputStreamChannel = null;
        FileChannel outputStreamChannel = null;
        try {
            inputStream = new FileInputStream(from);
            outputStream = new FileOutputStream(to);
            inputStreamChannel = inputStream.getChannel();
            outputStreamChannel = outputStream.getChannel();
            inputStreamChannel.transferTo(0, from.length(), outputStreamChannel);
        } catch (IOException e) {
            logger.error("copy file error, e:", e);
            throw e;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStreamChannel != null) {
                    inputStreamChannel.close();
                }
                if (outputStreamChannel != null) {
                    outputStreamChannel.close();
                }
            } catch (IOException e) {
                logger.error("close stream error, e:", e);
            }
        }
    }

    /**
     * 写入文件
     */
    private String writeFile(InputStream inputStream, int type) throws Exception {
        /*
        写入临时文件
         */
        String dirPath = InnerUtil.getWriteDirPath(type);
        String tmpFileName = InnerUtil.getFilePrefix();
        String filePath = dirPath + "/" + tmpFileName;
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filePath);
        } catch (Exception e) {
            logger.error("init error, e:", e);
            throw e;
        }

        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("write error, e:", e);
            throw e;
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                logger.error("close error, e:", e);
                throw e;
            }
        }

        /*
        读取type、宽、高等，修改为最终文件名
         */
        File file = new File(filePath);
        try {
            long length = file.length();
            MagicMatch match = Magic.getMagicMatch(file, false);
            String mimeType = match.getMimeType();
            ImageReader imageReader = getImageReaderByMimeType(mimeType);
            FileImageInputStream stream = new FileImageInputStream(file);
            imageReader.setInput(stream);
            int height = imageReader.getHeight(0);
            int width = imageReader.getWidth(0);
            File tmpFile = new File(filePath);
            String finalFileName = String.format("%s_%s_%d_%d_%d", tmpFileName, mimeType.substring(mimeType.indexOf('/') + 1), width, height, length);
            stream.close();
            File dstFile = new File(dirPath + "/" + finalFileName);
            tmpFile.renameTo(dstFile);
            return finalFileName;
        } catch (Exception e) {
            logger.error("read error, e:", e);
            file.delete();
            throw e;
        }
    }

    /**
     * 获取Reader
     */
    private ImageReader getImageReaderByMimeType(String mimeType) throws InvalidParameterException {
        switch (mimeType) {
            case MimeTypeUtils.IMAGE_JPEG_VALUE:
                return new JPEGImageReader(new JPEGImageReaderSpi());
            case MimeTypeUtils.IMAGE_GIF_VALUE:
                return new GIFImageReader(new GIFImageReaderSpi());
            case MimeTypeUtils.IMAGE_PNG_VALUE:
                return new PNGImageReader(new PNGImageReaderSpi());
            case "image/bmp":
                return new BMPImageReader(new BMPImageReaderSpi());
            default:
                throw new InvalidParameterException("not valid file type");
        }
    }

}
