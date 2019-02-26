package com.createarttechnology.imageserver.service;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.common.ErrorInfo;
import com.createarttechnology.imageserver.constants.ImageServerConstants;
import com.createarttechnology.logger.Logger;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by lixuhui on 2018/10/10.
 */
@Service
public class ReadService {

    private static final Logger logger = Logger.getLogger(ReadService.class);

    public String[] listRoot() {
        File root = new File(ImageServerConstants.ROOT_PATH);

        logger.info(ImageServerConstants.ROOT_PATH);
        String[] output = null;
        if (root.exists()) {
            output = root.list();
            Arrays.sort(output);
        }
        return output;
    }

    public BaseResp<String[]> listDir(String dirName) {
        File dir = new File(ImageServerConstants.ROOT_PATH + "/" + dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            return new BaseResp<>(ErrorInfo.RESOURCE_NOT_FOUND);
        }

        String[] files = dir.list();
        return new BaseResp<String[]>(ErrorInfo.SUCCESS).setData(files);
    }

    public void transferFile(String dirName, String fileName, HttpServletResponse response) {
        File file = new File(ImageServerConstants.ROOT_PATH + "/" + dirName + "/" + fileName);
        if (!file.exists()) {
            response.setStatus(404);
        }
        logger.info("filePath={}", file.getAbsolutePath());

        byte[] buffer = new byte[1024];
        OutputStream outputStream;
        InputStream inputStream;
        try {
            outputStream = response.getOutputStream();
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            logger.error("init error, e:", e);
            response.setStatus(500);
            return;
        }

        try {
            while (inputStream.read(buffer) != -1) {
                outputStream.write(buffer);
            }
            MagicMatch match = Magic.getMagicMatch(file, false);
            String mimeType = match.getMimeType();
            response.setContentType(mimeType);
            response.setStatus(200);
        } catch (Exception e) {
            logger.error("write error, e:", e);
            response.setStatus(500);
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                logger.error("close error, e:", e);
            }
        }
    }

}
