package com.gt.core.intercepors;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    public static String session_user_key = "session_user_key";

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        logger.info("拦截器日志  " + request.getRequestURL());
        logger.info("拦截器日志  " + request.getHeader("User-Agent"));
        String value = "";
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (session_user_key.equals(cookie.getName())) {
                    value = cookie.getValue();
                    break;
                }
            }
        }
        if (StringUtils.isEmpty(value)) {
            returnJson(response);
            return false;
        }

        HttpSession session = request.getSession();
        Object obj = session.getAttribute(value);
        logger.info("拦截器调用开始，user=" + JSON.toJSONString(obj));

        if (obj == null) {
            returnJson(response);
            return false;
        } else {
            logger.info("已登录...");
            return true;
        }
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

    private void returnJson(HttpServletResponse response) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print("未登录");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    static String[] agent = {"okhttp", "PostmanRuntime", "iphone", "iPhone", "IOS", "iOS", "ios", "android", "ipad", "phone", "mobile", "wap", "netfront", "java", "opera mobi",
            "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",
            "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
            "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",
            "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",
            "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
            "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",
            "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",
            "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",
            "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",
            "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",
            "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",
            "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v",
            "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",
            "Googlebot-Mobile"};

    /**
     * 判断User-Agent 是不是来自于手机
     *
     * @param ua
     * @return
     */
    public static boolean checkAgentIsMobile(String ua) {
        logger.info("获取登录角色：" + ua);
        boolean flag = false;
//		if (!ua.contains("Windows NT") || (ua.contains("Windows NT") && ua.contains("compatible; MSIE 9.0;"))) {
        // 排除 苹果桌面系统
//			if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
        for (String item : agent) {
            if (ua.contains(item)) {
                flag = true;
                break;
            }
        }
//			}
//		}
        if (!flag) {
            if (ua.contains("Windows") || ua.contains("Macintosh")) {
                flag = false;
            } else {
                flag = true;
            }
        }
        logger.info("是否手机端登录:" + flag);
        return flag;
    }
}
