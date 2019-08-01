package com.createarttechnology.imageserver.service;

import com.createarttechnology.imageserver.constants.ImageServerConstants;
import com.createarttechnology.imageserver.util.InnerUtil;
import com.createarttechnology.logger.Logger;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private WriteService writeService;

    /**
     * list根目录
     */
    public String[] listRoot(int type) {
        File root = new File(InnerUtil.getDirRootPath(type));
        String[] output = null;
        if (root.exists() && root.isDirectory()) {
            output = root.list();
            if (output != null) {
                Arrays.sort(output);
            }
        }
        return output;
    }

    /**
     * list目录
     */
    public String[] listDir(String dirName, int type) {
        File dir = new File(InnerUtil.getDirRootPath(type) + "/" + dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        return dir.list();
    }

    /**
     * 读文件
     */
    public void transferFile(String dirName, String fileName, HttpServletResponse response, int type) {
        File file = new File(InnerUtil.getDirRootPath(type) + "/" + dirName + "/" + fileName);
        if (!file.exists()) {
            // 预览图不存在，尝试将原图缩小，然后继续后续步骤
            if (type != ImageServerConstants.TYPE_PREVIEW || !writeService.scaleFile(dirName, fileName)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        byte[] buffer = new byte[1024];
        OutputStream outputStream;
        InputStream inputStream;
        try {
            outputStream = response.getOutputStream();
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            logger.error("init error, e:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        try {
            while (inputStream.read(buffer) != -1) {
                outputStream.write(buffer);
            }
            MagicMatch match = Magic.getMagicMatch(file, false);
            String mimeType = match.getMimeType();
            response.setContentType(mimeType);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.error("write error, e:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
