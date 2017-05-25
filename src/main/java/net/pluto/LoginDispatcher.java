package net.pluto;

import com.chinaamc.ta.util.GenericResult;
import net.pluto.exceptions.IncorrectInputException;
import net.pluto.login.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
        try {
            userManager.createUser(name, password);
            result.setCode(GenericResult.CODE_OK);
            result.setMessage("success");
        } catch (Exception e) {
            result.setCode(GenericResult.CODE_ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "signIn", method = RequestMethod.POST)
    public GenericResult signIn(@RequestParam("username") String name,
                                @RequestParam("password") String password,
                                HttpSession session) {
        GenericResult result = new GenericResult();
        try {
            userManager.validateUser(name, password);
            session.setAttribute("username", name);
            result.setCode(GenericResult.CODE_OK);
        } catch (IncorrectInputException e) {
            result.setCode(GenericResult.CODE_ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public GenericResult logout(HttpServletRequest request) {
        GenericResult result = new GenericResult();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("username");
            session.invalidate();
        }
        result.setCode(GenericResult.CODE_OK);
        return result;
    }
}
