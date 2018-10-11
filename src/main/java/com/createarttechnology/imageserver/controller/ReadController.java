package com.createarttechnology.imageserver.controller;

import com.createarttechnology.imageserver.service.ReadService;
import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.jutil.log.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lixuhui on 2018/10/10.
 */
@Controller
public class ReadController {

    private static final Logger logger = Logger.getLogger(ReadController.class);

    @Resource
    private ReadService readService;

    @RequestMapping(value = "/img/{picName}", method = RequestMethod.GET)
    public void getImage(@PathVariable("picName") String picName, HttpServletResponse response) {
        if (StringUtil.isEmpty(picName) || picName.length() < 25 || picName.indexOf('_') < 0) {
            response.setStatus(400);
        }
        String dirName = picName.substring(0, 8);
        String fileName = picName.substring(0, picName.indexOf('_'));
        if (StringUtil.isEmpty(dirName) || StringUtil.isEmpty(fileName)) {
            response.setStatus(400);
        }
        readService.transferFile(dirName, fileName, response);
    }

}
