package com.ozarychta.bebetter.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.ozarychta.bebetter.exception.InvalidTokenException;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;

public class TokenVerifier {

    private static TokenVerifier verifier;

    private GoogleIdTokenVerifier googleVerifier;

    private TokenVerifier() {

        String clientId = System.getenv("API_KEY");

        googleVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

    }

    public static TokenVerifier getInstance(){
        if(verifier == null){
            verifier = new TokenVerifier();
        }
        return verifier;
    }

    public VerifiedGoogleUser getVerifiedGoogleUser(String tokenString){
        if(tokenString == null){
            throw new InvalidTokenException("Authentication token string is null");
        }
        tokenString = tokenString.replace("Bearer ","");
        GoogleIdToken idToken = null;
        try {
            idToken = googleVerifier.verify(tokenString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException("Token validation failed");
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String userId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String)payload.get("name");

            if(!Strings.isBlank(userId)){
                return new VerifiedGoogleUser(userId, name, email);
            }
        }
        throw new InvalidTokenException("Invalid ID token");
    }
}
