package com.createarttechnology.imageserver.controller;

import com.createarttechnology.imageserver.bean.BaseResp;
import com.createarttechnology.imageserver.service.WriteService;
import com.createarttechnology.jutil.log.Logger;
import org.apache.commons.codec.binary.Base64InputStream;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by lixuhui on 2018/10/10.
 */
@RestController
public class WriteController {

    private static final Logger logger = Logger.getLogger(WriteController.class);

    @Resource
    private WriteService writeService;

    @RequestMapping(value = "/upload-pic-file", method = RequestMethod.POST)
    public BaseResp<String> uploadPicFile(@RequestParam MultipartFile picFile) {
        try {
            InputStream inputStream = picFile.getInputStream();
            return writeService.savePicFile(inputStream);
        } catch (Exception e) {
            logger.error("uploadPicFile error, e:", e);
            return new BaseResp<>();
        }
    }

    @RequestMapping(value = "/upload-pic-base64", method = RequestMethod.POST)
    public BaseResp<String> uploadPicBase64(@RequestParam String picBase64) {
        try {
            InputStream inputStream = new Base64InputStream(new ByteArrayInputStream(picBase64.getBytes()));
            return writeService.savePicFile(inputStream);
        } catch (Exception e) {
            logger.error("uploadPicBase64 error, e:", e);
            return new BaseResp<>();
        }
    }

    @RequestMapping(value = "/upload-pic-url", method = RequestMethod.POST)
    public BaseResp<String> uploadPicUrl(@RequestParam String picUrl) {
        try {
            URL url = new URL(picUrl);
            InputStream inputStream = url.openConnection().getInputStream();
            return writeService.savePicFile(inputStream);
        } catch (Exception e) {
            logger.error("uploadPicUrl error, e:", e);
            return new BaseResp<>();
        }
    }

}
