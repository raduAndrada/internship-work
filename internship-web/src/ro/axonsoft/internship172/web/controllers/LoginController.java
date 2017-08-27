package ro.axonsoft.internship172.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/login")
@Controller
public class LoginController {

    private static final String LOGIN_VIEW = "login";
    private static final String LOGIN_REDIRECT = "redirect:/candidates";

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping
    public String login() {
         String view = LOGIN_VIEW;
        if (SecurityContextHolder.getContext().getAuthentication()!=null && SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null)
        {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {
                view = LOGIN_REDIRECT;
            }

        }
        return view;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String login(@RequestParam final String username, @RequestParam final String password) {
        return LOGIN_VIEW;
    }

}
