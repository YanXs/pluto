package net.pluto;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DefaultHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (isWelcomePage(request)) {
            staleCache(response);
            if (isSessionActive(request.getSession())) {
                modelAndView.setViewName("forward:backups.html");
            }
        }
        if (isLogin(request)) {
            staleCache(response);
            if (isSessionActive(request.getSession())) {
                modelAndView.setViewName("forward:backups.html");
            }
        }
        if (isRegister(request)) {
            staleCache(response);
            if (isSessionActive(request.getSession())) {
                modelAndView.setViewName("forward:backups.html");
            } else {
                modelAndView.setViewName("forward:signin.html");
            }
        }
    }

    private void staleCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
    }

    private boolean isSessionActive(HttpSession session) {
        return session.getAttribute("username") != null;
    }

    private boolean isWelcomePage(HttpServletRequest request) {
        String path = getPath(request);
        return path.equals("/") || path.equals("/signin.html");
    }

    private boolean isLogin(HttpServletRequest request) {
        return getPath(request).equals("/singIn");
    }

    private boolean isRegister(HttpServletRequest request) {
        return getPath(request).equals("/singUP");
    }

    private String getPath(HttpServletRequest request) {
        return (String) request.getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping");
    }
}
