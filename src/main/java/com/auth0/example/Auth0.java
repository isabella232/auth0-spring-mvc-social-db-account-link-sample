package com.auth0.example;


import com.auth0.Auth0User;
import com.auth0.Tokens;

public interface Auth0 {

    Auth0User linkAccount(Auth0User user, Tokens tokens, Tokens existingTokens);

    String getManagementToken(final String domain, final String clientId, final String clientSecret);

    boolean isDatabaseUser(final Auth0User auth0User, final String expectedConnection);

    boolean hasPasswordUserProfile(Auth0User auth0User, String domain, String managementToken, String connection);

}
