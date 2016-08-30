package com.auth0.example;

import com.auth0.Auth0Client;
import com.auth0.Auth0User;
import com.auth0.Tokens;
import com.auth0.authentication.result.UserIdentity;
import com.auth0.web.Auth0Config;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import java.net.URLEncoder;
import java.util.List;
import java.util.function.BiFunction;

import static us.monoid.web.Resty.content;

/**
 * Wrapper implementation around Auth0 service calls
 * Don't expose internals of Auth0 library
 */
@Service
public class Auth0Impl implements Auth0 {

    protected Auth0Client auth0Client;
    protected Auth0Config auth0Config;
    protected AppConfig appConfig;

    @Autowired
    public Auth0Impl(final Auth0Client auth0Client, final Auth0Config auth0Config, final AppConfig appConfig) {
        Validate.notNull(auth0Client);
        Validate.notNull(auth0Config);
        Validate.notNull(appConfig);
        this.auth0Client = auth0Client;
        this.auth0Config = auth0Config;
        this.appConfig = appConfig;
    }

    @Override
    public Auth0User linkAccount(final Auth0User user, final Tokens tokens, final Tokens existingTokens) {
        // link accounts here
        final String primaryAccountUserId = user.getUserId();
        final String primaryAccountJwt = tokens.getIdToken();
        final String secondaryAccountJwt = existingTokens.getIdToken();
        // do account linking
        try {
            final String encodedPrimaryAccountUserId = URLEncoder.encode(primaryAccountUserId, "UTF-8");
            final String linkUri = getUri("/api/v2/users/") + encodedPrimaryAccountUserId + "/identities";
            final Resty resty = new Resty();
            resty.withHeader("Authorization", "Bearer " + primaryAccountJwt);
            final JSONObject json = new JSONObject();
            json.put("link_with", secondaryAccountJwt);
            final JSONResource linkedProfileInfo = resty.json(linkUri, content(json));
            final JSONArray profileArray = linkedProfileInfo.array();
            final JSONObject firstProfileEntry = profileArray.getJSONObject(0);
            final String primaryConnectionType = (String) firstProfileEntry.get("connection");
            final String expectedPrimaryConnectionType = appConfig.getPasswordConnection();
            if (!expectedPrimaryConnectionType.equals(primaryConnectionType)) {
                throw new IllegalStateException("Error linking accounts - wrong primary connection type detected: " + primaryConnectionType);
            }
            // Just fetch updated (linked) profile using previously obtained tokens for profile
            final Auth0User linkedUser = auth0Client.getUserProfile(tokens);
            return linkedUser;
        } catch (Exception ex) {
            throw new IllegalStateException("Error retrieving profile information from Auth0", ex);
        }
    }

    protected String getUri(String path) {
        return String.format("https://%s%s", auth0Config.getDomain(), path);
    }

    @Override
    public boolean hasPasswordUserProfile(final Auth0User auth0User, final String domain, final String managementToken, final String connection) {
        if (isDatabaseUser(auth0User, connection)) {
            return true;
        }
        final BiFunction<String, String, String> getUri = (auth0Domain, path) -> String.format("https://%s%s", auth0Domain, path);
        final String email = auth0User.getEmail();
        if (email == null) {
            throw new IllegalStateException("Error Auth0");
        }
        try {
            final StringBuilder pathBuilder = new StringBuilder("/api/v2/users?");
            pathBuilder
                    .append("q=").append(URLEncoder.encode("email:", "UTF-8")).append("\"").append(email).append("\"")
                    .append(" AND ").append(URLEncoder.encode("identities.connection:", "UTF-8")).append("\"").append(connection).append("\"")
                    .append("&search_engine=v2");
            final String path = pathBuilder.toString();
            final String url = getUri.apply(domain, path);
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("authorization", "Bearer " + managementToken)
                    .addHeader("cache-control", "no-cache")
                    .build();

            final Response response = client.newCall(request).execute();
            if (response.code() != 200) {
                // TODO - improve error handling
                throw new IllegalStateException("Error occurred searching for user by email: " + email);
            }
            final String result = response.body().string();
            // assume something other than empty array indicates results
            return !"[]".equals(result);

        } catch (Exception e) {
            throw new IllegalStateException("Error checking database info for user: ", e);
        }
    }

    @Override
    public boolean isDatabaseUser(final Auth0User auth0User, final String expectedConnection) {
        Validate.notNull(expectedConnection);
        final List<UserIdentity> identities = auth0User.getIdentities();
        if (identities == null || identities.isEmpty()) {
            return false;
        }
        // primary identity always listed first
        final UserIdentity primaryIdentity = identities.get(0);
        final String connection = primaryIdentity.getConnection();
        return expectedConnection.equals(connection);
    }

}
