package com.auth0.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties("auth0")
public class AppConfig {

    @Value(value = "${auth0.managementToken}")
    protected String managementToken;

    @Value(value = "${auth0.passwordConnection}")
    protected String passwordConnection;

    @Value(value = "${auth0.socialConnection}")
    protected String socialConnection;

    @Value(value = "${auth0.accountLinkRedirectOnSuccess}")
    protected String accountLinkRedirectOnSuccess;

    @Value(value = "${auth0.accountLinkRedirectOnFail}")
    protected String accountLinkRedirectOnFail;


    public String getManagementToken() {
        return managementToken;
    }

    public String getPasswordConnection() {
        return passwordConnection;
    }

    public String getSocialConnection() {
        return socialConnection;
    }

    public String getAccountLinkRedirectOnSuccess() {
        return accountLinkRedirectOnSuccess;
    }

    public String getAccountLinkRedirectOnFail() {
        return accountLinkRedirectOnFail;
    }
}
