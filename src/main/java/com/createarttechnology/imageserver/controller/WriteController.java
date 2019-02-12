package com.createarttechnology.imageserver.controller;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.imageserver.service.WriteService;
import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;
import com.google.common.collect.Maps;
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
import java.util.Map;

/**
 * Created by lixuhui on 2018/10/10.
 */
@RestController
public class WriteController {

    private static final Logger logger = Logger.getLogger(WriteController.class);

    @Resource
    private WriteService writeService;

    @RequestMapping(value = "/upload/file", method = RequestMethod.POST)
    public BaseResp<String> uploadPicFile(@RequestParam MultipartFile picFile) {
        try {
            InputStream inputStream = picFile.getInputStream();
            return writeService.savePicFile(inputStream);
        } catch (Exception e) {
            logger.error("uploadPicFile error, e:", e);
            return new BaseResp<>();
        }
    }

    @RequestMapping(value = "/upload/base64", method = RequestMethod.POST)
    public BaseResp<String> uploadPicBase64(@RequestParam String picBase64) {
        if (StringUtil.isNotEmpty(picBase64) && picBase64.startsWith("data:image/png;base64,")) {
            picBase64 = picBase64.substring(picBase64.indexOf(','));
        }
        try {
            InputStream inputStream = new Base64InputStream(new ByteArrayInputStream(picBase64.getBytes()));
            return writeService.savePicFile(inputStream);
        } catch (Exception e) {
            logger.error("uploadPicBase64 error, e:", e);
            return new BaseResp<>();
        }
    }

    @RequestMapping(value = "/upload/url", method = RequestMethod.POST)
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

    @RequestMapping(value = "/upload/editormd-image-file", method = RequestMethod.POST)
    public Map<String, Object> uploadEditormdImageFile(@RequestParam("editormd-image-file") MultipartFile picFile) {
        Map<String, Object> resp = Maps.newHashMap();
        try {
            InputStream inputStream = picFile.getInputStream();
            BaseResp<String> saveResp = writeService.savePicFile(inputStream);
            resp.put("success", saveResp.success() ? 1 : 0);
            resp.put("message", saveResp.getMsg());
            if (saveResp.success()) {
                resp.put("url", "/img/" + saveResp.getData());
            }
        } catch (Exception e) {
            logger.error("uploadPicFile error, e:", e);
            resp.put("success", "0");
            resp.put("message", e.getMessage());
        }
        return resp;
    }

}
