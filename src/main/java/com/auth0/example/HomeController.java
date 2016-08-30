package com.auth0.example;

import com.auth0.Auth0User;
import com.auth0.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AppConfig appConfig;

    @Autowired
    protected Auth0 auth0;

    @Autowired
    public HomeController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @RequestMapping(value="/portal/home", method = RequestMethod.GET)
    protected String home(final Map<String, Object> model, final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        logger.info("Home page");
        final Auth0User user = SessionUtils.getAuth0User(req);
        final HttpSession session = req.getSession(true);
        final boolean hasPasswordUserProfile = (Boolean) session.getAttribute("hasPasswordUserProfile");
        model.put("user", user);
        model.put("passwordConnection", appConfig.getPasswordConnection());
        // TODO - here we assume user has email - some social connections may not enforce this
        model.put("email", user.getEmail());
        final boolean hasPasswordLogin = auth0.isDatabaseUser(user, appConfig.getPasswordConnection());
        // user is not logged in with a password connection profile, AND a matching password profile exists
        // we want the user to therefore re-enter their password profile so we can link with current profile
        req.setAttribute("passwordLogin", !hasPasswordLogin && hasPasswordUserProfile);
        return "home";
    }

}
