package com.ozarychta;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class TokenVerifier {

    private static final String CLIENT_ID =
            "576716243653-528khc4t2dv9oe1u24j38ohqdttvpghl.apps.googleusercontent.com";


    private static TokenVerifier verifier;

    private GoogleIdTokenVerifier googleVerifier;

    private TokenVerifier() {
        googleVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Arrays.asList(CLIENT_ID))
                .build();
    }

    public static TokenVerifier getInstance(){
        if(verifier == null){
            verifier = new TokenVerifier();
        }
        return verifier;
    }

    public VerifiedGoogleUserId getGoogleUserId(String tokenString){

        tokenString = tokenString.replace("Bearer ","");
        GoogleIdToken idToken = null;
        try {
            idToken = googleVerifier.verify(tokenString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return new VerifiedGoogleUserId(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            return new VerifiedGoogleUserId(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String userId = payload.getSubject();
            String email = payload.getEmail();

            if(!StringUtils.isEmpty(userId)){
                return new VerifiedGoogleUserId(userId, email, HttpStatus.OK);
            }
        }
        return new VerifiedGoogleUserId(null, null, HttpStatus.UNAUTHORIZED);
    }

}
