package net.pluto;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DefaultHandlerInterceptor extends HandlerInterceptorAdapter {


    private boolean isSessionActive(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (isWelcomePage(request)) {
            response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
            if (isSessionActive(request.getSession())) {
                modelAndView.setViewName("forward:backups.html");
            }
        }
        if (isLogin(request)) {
            response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
            if (isSessionActive(request.getSession())) {
                modelAndView.setViewName("forward:backups.html");
            }
        }
        if (isRegister(request)) {
            response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
            if (isSessionActive(request.getSession())) {
                modelAndView.setViewName("forward:backups.html");
            } else {
                modelAndView.setViewName("forward:signin.html");
            }
        }
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }


    private boolean isWelcomePage(HttpServletRequest request) {
        String path = (String) request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping");
        return path.equals("/") || path.equals("/signin.html");
    }

    private boolean isError(HttpServletRequest request) {
        return request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping").equals("/error");
    }

    private boolean isLogin(HttpServletRequest request) {
        return request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping").equals("/singIn");
    }

    private boolean isRegister(HttpServletRequest request) {
        return request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping").equals("/singUP");
    }
}
