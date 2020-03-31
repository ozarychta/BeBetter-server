package com.ozarychta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints={@UniqueConstraint(columnNames = "google_user_id")})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_user_id")
    private String googleUserId;

    private String username;

    private Integer rankingPoints;

    private Integer highestStreak;

    private String aboutMe;

    private String mainGoal;

    @JsonIgnore
    @OneToMany(mappedBy="creator")
    private List<Challenge> createdChallenges = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    private List<Challenge> challenges = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name="users_friends",
            joinColumns={@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="friends_id")})
    private List<User> friends = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "friends")
    private List<User> friends2 = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private List<Day> days = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="creator")
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="invitated")
    private List<Invitation> receivedInvitations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="invitator")
    private List<Invitation> sentInvitations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private List<UserAchievement> achievements = new ArrayList<>();

    public List<Invitation> getReceivedInvitations() {
        return receivedInvitations;
    }

    public void setReceivedInvitations(List<Invitation> receivedInvitations) {
        this.receivedInvitations = receivedInvitations;
    }

    public List<Invitation> getSentInvitations() {
        return sentInvitations;
    }

    public void setSentInvitations(List<Invitation> sentInvitations) {
        this.sentInvitations = sentInvitations;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleUserId() {
        return googleUserId;
    }

    public void setGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRankingPoints() {
        return rankingPoints;
    }

    public void setRankingPoints(Integer rankingPoints) {
        this.rankingPoints = rankingPoints;
    }

    public Integer getHighestStreak() {
        return highestStreak;
    }

    public void setHighestStreak(Integer highestStreak) {
        this.highestStreak = highestStreak;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getMainGoal() {
        return mainGoal;
    }

    public void setMainGoal(String mainGoal) {
        this.mainGoal = mainGoal;
    }

    public List<Challenge> getCreatedChallenges() {
        return createdChallenges;
    }

    public void setCreatedChallenges(List<Challenge> createdChallenges) {
        this.createdChallenges = createdChallenges;
    }

    public List<User> getFriends2() {
        return friends2;
    }

    public void setFriends2(List<User> friends2) {
        this.friends2 = friends2;
    }

    public List<UserAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<UserAchievement> achievements) {
        this.achievements = achievements;
    }
}
