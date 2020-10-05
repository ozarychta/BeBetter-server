package com.ozarychta.bebetter.model;

import com.ozarychta.bebetter.enums.AnswerType;
import com.ozarychta.bebetter.enums.InvitationType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "invitations")
public class Invitation implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private InvitationType type;

    private String message;

    private AnswerType answerType;

    @ManyToOne
    private Challenge challenge;

    @ManyToOne
    private User invitated;

    @ManyToOne
    private User invitator;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InvitationType getType() {
        return type;
    }

    public void setType(InvitationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }
}
