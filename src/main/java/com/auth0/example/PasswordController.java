package com.auth0.example;

import com.auth0.Auth0User;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.auth0.web.Auth0Config;
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
public class PasswordController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Auth0Config auth0Config;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    Auth0 auth0;


    @RequestMapping(value="/portal/password", method = RequestMethod.GET)
    protected String password(final Map<String, Object> model, final HttpServletRequest req,
                              final HttpServletResponse res) throws ServletException, IOException {
        final Auth0User user = SessionUtils.getAuth0User(req);
        if (user == null) {
            final String logoutPath = auth0Config.getOnLogoutRedirectTo();
            res.sendRedirect(logoutPath);
        } else if (user.getEmail() == null) {
            final String home = auth0Config.getLoginRedirectOnSuccess();
            res.sendRedirect(home);
        } else if (auth0.isDatabaseUser(user, appConfig.getPasswordConnection())) {
            final String home = auth0Config.getLoginRedirectOnSuccess();
            res.sendRedirect(home);
        } else {
            final HttpSession session = req.getSession(true);
            final boolean hasPasswordUserProfile = (Boolean) session.getAttribute("hasPasswordUserProfile");
            if (! hasPasswordUserProfile) {
                final String home = auth0Config.getLoginRedirectOnSuccess();
                res.sendRedirect(home);
            } else {
                model.put("email", user.getEmail());
                req.setAttribute("user", user);
                // add Nonce to storage
                NonceUtils.addNonceToStorage(req);
                logger.debug("Performing login");
                detectError(model);
                model.put("clientId", auth0Config.getClientId());
                model.put("domain", auth0Config.getDomain());
                model.put("passwordConnection", appConfig.getPasswordConnection());
                model.put("state", SessionUtils.getState(req));
            }
        }
        return "password";
    }

    private void detectError(final Map<String, Object> model) {
        if (model.get("error") != null) {
            model.put("error", true);
        } else {
            model.put("error", false);
        }
    }

}
