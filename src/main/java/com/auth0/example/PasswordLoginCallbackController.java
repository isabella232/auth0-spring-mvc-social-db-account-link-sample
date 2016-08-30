package com.auth0.example;

import com.auth0.Auth0User;
import com.auth0.NonceUtils;
import com.auth0.SessionUtils;
import com.auth0.Tokens;
import com.auth0.web.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class PasswordLoginCallbackController extends Auth0CallbackHandler
        implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    protected AppConfig appConfig;

    @Autowired
    protected Auth0 auth0;

    /**
     * Override with our values
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.redirectOnSuccess = appConfig.getAccountLinkRedirectOnSuccess();
        this.redirectOnFail = appConfig.getAccountLinkRedirectOnFail();
    }

    @RequestMapping(value="/plcallback", method = RequestMethod.GET)
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res)
            throws ServletException, IOException {
        if (isValidRequest(req)) {
            try {
                final Tokens tokens = fetchTokens(req);
                Auth0User auth0User = auth0Client.getUserProfile(tokens);
                auth0User = handleAccountLink(auth0User, req, tokens);
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

    protected Auth0User handleAccountLink(final Auth0User user, final HttpServletRequest req, final Tokens tokens) {
        // check here whether account linking is required..
        if (auth0.isDatabaseUser(user, appConfig.getPasswordConnection()) && !isLinkedAccount(user)) {
            final Auth0User existingUser = SessionUtils.getAuth0User(req);
            final Tokens existingTokens = SessionUtils.getTokens(req);
            if (existingUser != null && !auth0.isDatabaseUser(existingUser, appConfig.getPasswordConnection()))  {
                return auth0.linkAccount(user, tokens, existingTokens);
            }
        }
        // just return the existing user
        return user;
    }

    protected boolean isLinkedAccount(final Auth0User auth0User) {
        return auth0User.getIdentities() != null && auth0User.getIdentities().size() > 1;
    }

}
