package com.ozarychta;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class VerifiedGoogleUserId {

    private String googleUserId;
    private String name;
    private String email;
    private HttpStatus httpStatus;

    public VerifiedGoogleUserId(String googleUserId, String email, HttpStatus httpStatus) {
        this.googleUserId = googleUserId;
        this.email = email;
        this.httpStatus = httpStatus;
    }

    public VerifiedGoogleUserId(String googleUserId, String name, String email, HttpStatus httpStatus) {
        this.googleUserId = googleUserId;
        this.name = name;
        this.email = email;
        this.httpStatus = httpStatus;
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

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerifiedGoogleUserId that = (VerifiedGoogleUserId) o;
        return Objects.equals(googleUserId, that.googleUserId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                httpStatus == that.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(googleUserId, name, email, httpStatus);
    }
}
