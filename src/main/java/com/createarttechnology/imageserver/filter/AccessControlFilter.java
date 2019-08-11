package com.createarttechnology.imageserver.filter;

import com.createarttechnology.jutil.AntiBotUtil;
import com.createarttechnology.jutil.StringUtil;
import com.createarttechnology.logger.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by lixuhui on 2018/10/10.
 */
public class AccessControlFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String referer = req.getHeader("Referer");
        if (StringUtil.isNotEmpty(referer) && !(referer.startsWith("http://local") || referer.startsWith("http://www.createarttechnology.com") || referer.startsWith("http://createarttechnology.com"))) {
            ((HttpServletResponse) response).setStatus(403);
            return;
        }

        chain.doFilter(request, response);

    }

    public void destroy() {

    }
}
