package com.ozarychta;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class VerifiedGoogleUserId {

    private String googleUserId;
    private String name;
    private String email;

    public VerifiedGoogleUserId(String googleUserId, String email) {
        this.googleUserId = googleUserId;
        this.email = email;
    }

    public VerifiedGoogleUserId(String googleUserId, String name, String email) {
        this.googleUserId = googleUserId;
        this.name = name;
        this.email = email;
    }

    public String getGoogleUserId() {
        return googleUserId;
    }

    public void setGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
