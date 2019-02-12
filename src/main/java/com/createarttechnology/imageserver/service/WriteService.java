package com.createarttechnology.imageserver.service;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.common.ErrorInfo;
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
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.security.InvalidParameterException;

/**
 * Created by lixuhui on 2018/10/10.
 */
@Service
public class WriteService {

    private static final Logger logger = Logger.getLogger(WriteService.class);

    @PostConstruct
    public void init() {
        File rootPath = new File(ImageServerConstants.ROOT_PATH);
        if (!rootPath.exists()) {
            rootPath.mkdir();
        }
    }

    public BaseResp<String> savePicFile(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return new BaseResp<>(ErrorInfo.INVALID_PARAMS);
        }
        try {
            String picFileName = writeFile(inputStream);
            logger.info("write finished, picFileName={}", picFileName);
            return new BaseResp<String>(ErrorInfo.SUCCESS).setData(picFileName);
        } catch (Exception e) {
            logger.error("write file error, e:", e);
            throw e;
        } finally {
            inputStream.close();
        }
    }

    private String writeFile(InputStream inputStream) throws Exception {
        String dirPath = InnerUtil.getDirPath();
        String fileName = InnerUtil.getFileName();
        String filePath = dirPath + "/" + fileName;
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
            String finalFileName = String.format("%s_%s_%d_%d_%d", fileName, mimeType.substring(mimeType.indexOf('/') + 1), width, height, length);
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

    private ImageReader getImageReaderByMimeType(String mimeType) throws Exception {
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
