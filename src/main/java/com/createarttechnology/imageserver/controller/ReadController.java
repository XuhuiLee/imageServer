package com.createarttechnology.imageserver.controller;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.common.ErrorInfo;
import com.createarttechnology.imageserver.bean.PicBean;
import com.createarttechnology.imageserver.bean.WaterfallBean;
import com.createarttechnology.imageserver.service.ReadService;
import com.createarttechnology.imageserver.util.InnerUtil;
import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private static Pattern DIR_NAME_PATTERN = Pattern.compile("\\d{8}");

    private static Pattern PIC_LONG_NAME_PATTERN = Pattern.compile("\\d{19}_\\w{3,4}_(\\d+)_(\\d+)_\\d+");

    @Resource
    private ReadService readService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listRoot(Model model) {
        model.addAttribute("dirs", readService.listRoot());

        return "page/list";
    }

    @RequestMapping(value = "/dir/{dirName}", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp<String[]> listDir(@PathVariable String dirName, HttpServletResponse response) {
        if (Strings.isNullOrEmpty(dirName) || dirName.length() != 8 || !DIR_NAME_PATTERN.matcher(dirName).matches()) {
            response.setStatus(400);
            return null;
        }

        return readService.listDir(dirName);
    }

    @RequestMapping(value = "/pic/{picName}", method = RequestMethod.GET)
    public void getImage(@PathVariable String picName, HttpServletResponse response) {
        if (StringUtil.isEmpty(picName) || !PIC_LONG_NAME_PATTERN.matcher(picName).matches()) {
            response.setStatus(400);
            return;
        }
        String dirName = picName.substring(0, 8);
        String fileName = picName;
        if (StringUtil.isEmpty(dirName) || StringUtil.isEmpty(fileName)) {
            response.setStatus(400);
            return;
        }
        readService.transferFile(dirName, fileName, response);
    }

    @RequestMapping(value = "/waterfall", method = RequestMethod.GET)
    @ResponseBody
    public BaseResp getWaterfall(@RequestParam(required = false) String dir) {
        logger.info("request dirName={}", dir);
        String[] dirs = readService.listRoot();
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
        BaseResp<String[]> filesResp = readService.listDir(dir);
        if (filesResp.success()) {
            String[] files = filesResp.getData();
            if (files != null) {
                for (String file : files) {
                    WaterfallBean bean = new WaterfallBean();
                    PicBean picBean = InnerUtil.parsePicBean(file);
                    if (picBean != null) {
                        bean.setTitle(picBean.getTitle());
                        bean.setSrc("/img/pic/" + picBean.getFileName());
                        if (picBean.getWidth() <= 190) {
                            double scale = 190d / picBean.getWidth();
                            bean.setHeight((int) (picBean.getHeight() * scale));
                            bean.setWidth(190);
                        } else {
                            double scale = picBean.getWidth() / 190d;
                            bean.setHeight((int) (picBean.getHeight() / scale));
                            bean.setWidth(190);
                        }
                        result.add(bean);
                    }
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
