package com.gt.core.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.gt.core.intercepors.LoginInterceptor.session_user_key;

public class SessionUtils {

    /**
     * 存session
     *
     * @param req
     * @param reps
     * @param obj
     */
    public static void save(HttpServletRequest req, HttpServletResponse reps, Object obj) {
        String token = TokenProccessor.getInstance().makeToken();
        HttpSession session = req.getSession();
        session.setAttribute(token, obj);
        Cookie cookie = new Cookie(session_user_key, token);
        cookie.setPath("/");
        reps.addCookie(cookie);
    }
}
