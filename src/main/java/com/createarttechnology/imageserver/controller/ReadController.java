package com.createarttechnology.imageserver.controller;

import com.createarttechnology.common.BaseResp;
import com.createarttechnology.imageserver.service.ReadService;
import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;
import com.google.common.base.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * Created by lixuhui on 2018/10/10.
 */
@Controller
public class ReadController {

    private static final Logger logger = Logger.getLogger(ReadController.class);

    private static Pattern DIR_NAME_PATTERN = Pattern.compile("\\d{8}");

    private static Pattern PIC_LONG_NAME_PATTERN = Pattern.compile("\\d{19}_\\w{3,4}_\\d+_\\d+_\\d+");
    private static Pattern PIC_SHORT_NAME_PATTERN = Pattern.compile("\\d{19}");

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
        }

        return readService.listDir(dirName);
    }

    @RequestMapping(value = "/pic/{picName}", method = RequestMethod.GET)
    public void getImage(@PathVariable String picName, HttpServletResponse response) {
        if (StringUtil.isEmpty(picName) || !(PIC_LONG_NAME_PATTERN.matcher(picName).matches() || PIC_SHORT_NAME_PATTERN.matcher(picName).matches())) {
            response.setStatus(400);
        }
        String dirName = picName.substring(0, 8);
        String fileName = picName;
        if (fileName.contains("_")) {
            fileName = fileName.substring(0, fileName.indexOf('_'));
        }
        if (StringUtil.isEmpty(dirName) || StringUtil.isEmpty(fileName)) {
            response.setStatus(400);
        }
        readService.transferFile(dirName, fileName, response);
    }

}
