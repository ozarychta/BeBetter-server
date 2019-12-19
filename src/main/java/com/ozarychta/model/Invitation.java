package com.ozarychta.model;

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

    private Answer answer;

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

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
