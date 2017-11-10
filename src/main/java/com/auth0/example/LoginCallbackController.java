package com.auth0.example;

import com.auth0.Auth0User;
import com.auth0.NonceUtils;
import com.auth0.Tokens;
import com.auth0.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginCallbackController extends Auth0CallbackHandler {

    @Autowired
    private Auth0Config auth0Config;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private Auth0 auth0;


    @RequestMapping(value = "${auth0.loginCallback}", method = RequestMethod.GET)
    protected void callback(final HttpServletRequest req, final HttpServletResponse res)
            throws ServletException, IOException {
        if (isValidRequest(req)) {
            try {
                final Tokens tokens = fetchTokens(req);
                final Auth0User auth0User = auth0Client.getUserProfile(tokens);
                final String connection = appConfig.getPasswordConnection();
                final String domain = auth0Config.getDomain();
                final String clientId = auth0Config.getClientId();
                final String clientSecret = auth0Config.getClientSecret();
                final String managementToken = auth0.getManagementToken(domain, clientId, clientSecret);
                store(auth0.hasPasswordUserProfile(auth0User, domain, managementToken, connection), req);
                store(tokens, auth0User, req);
                NonceUtils.removeNonceFromStorage(req);
                onSuccess(req, res);
            } catch (IllegalArgumentException ex) {
                onFailure(req, res, ex);
            } catch (IllegalStateException ex) {
                onFailure(req, res, ex);
            }
        } else {
            onFailure(req, res, new IllegalStateException("Invalid state or error"));
        }
    }

    protected void store(final Boolean hasPasswordUserProfile, final HttpServletRequest req) {
        final HttpSession session = req.getSession(true);
        session.setAttribute("hasPasswordUserProfile", hasPasswordUserProfile);
    }

}
