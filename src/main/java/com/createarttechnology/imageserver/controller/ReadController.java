package com.createarttechnology.imageserver.controller;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.common.ErrorInfo;
import com.createarttechnology.imageserver.bean.PicBean;
import com.createarttechnology.imageserver.bean.WaterfallBean;
import com.createarttechnology.imageserver.constants.ImageServerConstants;
import com.createarttechnology.imageserver.service.ReadService;
import com.createarttechnology.imageserver.util.InnerUtil;
import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by lixuhui on 2018/10/10.
 */
@Controller
public class ReadController {

    private static final Logger logger = Logger.getLogger(ReadController.class);

    private static Pattern PIC_LONG_NAME_PATTERN = Pattern.compile("\\d{19}_\\w{3,4}_(\\d+)_(\\d+)_\\d+");

    private static final int WATER_FALL_WIDTH_INT = 190;
    private static final double WATER_FALL_WIDTH_DOUBLE = 190;

    @Resource
    private ReadService readService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listRoot() {
        return "page/list";
    }

    @RequestMapping(value = "/pic/{picName}", method = RequestMethod.GET)
    public void getImage(@PathVariable String picName, HttpServletResponse response) {
        if (StringUtil.isEmpty(picName) || !PIC_LONG_NAME_PATTERN.matcher(picName).matches()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String dirName = picName.substring(0, 8);
        String fileName = picName;
        if (StringUtil.isEmpty(dirName) || StringUtil.isEmpty(fileName)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        readService.transferFile(dirName, fileName, response, ImageServerConstants.TYPE_IMAGE);
    }

    @RequestMapping(value = "/prev/{picName}", method = RequestMethod.GET)
    public void getPreview(@PathVariable String picName, HttpServletResponse response) {
        if (StringUtil.isEmpty(picName) || !PIC_LONG_NAME_PATTERN.matcher(picName).matches()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String dirName = picName.substring(0, 8);
        String fileName = picName;
        if (StringUtil.isEmpty(dirName) || StringUtil.isEmpty(fileName)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        readService.transferFile(dirName, fileName, response, ImageServerConstants.TYPE_PREVIEW);
    }

    @RequestMapping(value = "/waterfall", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp getWaterfall(@RequestParam(required = false) String dir) {
        // 从/image读，前端使用预览图
        String[] dirs = readService.listRoot(ImageServerConstants.TYPE_IMAGE);
        if (dirs == null || dirs.length <= 0) {
            return new BaseResp<>(ErrorInfo.INVALID_PARAMS);
        } else if (StringUtil.isEmpty(dir)) {
            dir = dirs[0];
        }
        int idx = -1;
        for (int i = 0; i < dirs.length; i++) {
            if (dirs[i].equals(dir)) {
                idx = i;
            }
        }
        if (idx == -1) {
            return new BaseResp<>(ErrorInfo.INVALID_PARAMS);
        }

        List<WaterfallBean> result = Lists.newArrayList();
        // 从/image读，前端使用预览图
        String[] files = readService.listDir(dir, ImageServerConstants.TYPE_IMAGE);
        if (files != null) {
            for (String file : files) {
                WaterfallBean bean = new WaterfallBean();
                PicBean picBean = InnerUtil.parsePicBean(file);
                if (picBean != null) {
                    bean.setTitle(picBean.getTitle());
                    bean.setSrc(picBean.getFileName());
                    if (picBean.getWidth() <= WATER_FALL_WIDTH_INT) {
                        double scale = WATER_FALL_WIDTH_DOUBLE / picBean.getWidth();
                        bean.setHeight((int) (picBean.getHeight() * scale));
                        bean.setWidth(WATER_FALL_WIDTH_INT);
                    } else {
                        double scale = picBean.getWidth() / WATER_FALL_WIDTH_DOUBLE;
                        bean.setHeight((int) (picBean.getHeight() / scale));
                        bean.setWidth(WATER_FALL_WIDTH_INT);
                    }
                    result.add(bean);
                }
            }
        }
        BaseResp<List<WaterfallBean>> resp = new BaseResp<>();
        resp.setData(result);
        if (idx == dirs.length - 1) {
            resp.setErrorInfo(ErrorInfo.NO_MORE_DATA);
        } else {
            resp.setErrorInfo(ErrorInfo.SUCCESS);
            resp.setLocateAnchor(dirs[idx + 1]);
        }
        return resp;
    }

}
