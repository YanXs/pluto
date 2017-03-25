package com.chinaamc.pluto;

import com.chinaamc.pluto.login.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/")
public class LoginDispatcher {

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView();
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            mv.setViewName("/backups.html");
        } else {
            mv.setViewName("/signin.html");
        }
        response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
        return mv;
    }

    @RequestMapping(value = "signUp", method = RequestMethod.POST)
    public GenericResult signUp(@RequestParam("usernamesignup") String name,
                               @RequestParam("passwordsignup") String password) {
        GenericResult result = new GenericResult();
        if (name.length() == 0 || password.length() == 0) {
            result.setCode(GenericResult.CODE_PENDING);
            result.setMessage("name and password cannot be empty");
        } else if (userManager.usernameExists(name)) {
            result.setCode(GenericResult.CODE_PENDING);
            result.setMessage("user: [" + name + "] already registered");
        } else {
            userManager.createUser(name, password);
            result.setCode(GenericResult.CODE_OK);
            result.setMessage("user: [" + name + "] registered");
        }
        return result;
    }

    @RequestMapping(value = "signIn", method = RequestMethod.GET)
    public ModelAndView signIn(@RequestParam("username") String name,
                               @RequestParam("password") String password,
                               HttpServletResponse response,
                               HttpSession session) {
        userManager.validateUser(name, password);
        session.setAttribute("username", name);
        response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
        return new ModelAndView(new RedirectView("http://localhost:9092"));
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ModelAndView logout(HttpServletResponse response, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("username");
            session.invalidate();
        }
        response.setHeader("Cache-Control", "no-cache,no-store, max-age=0");
        return new ModelAndView(new RedirectView("http://localhost:9092"));
    }
}
