package com.ozarychta.bebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_achievements")
public class UserAchievement implements Serializable {

//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    private Long id;

    @EmbeddedId
    private UserAchievementId id;

    private Boolean achieved;

    @JsonIgnore
    @ManyToOne
    @MapsId("userId")
    private User user;

    @JsonIgnore
    @ManyToOne
    @MapsId("achievementId")
    private Achievement achievement;

    public UserAchievement() {
    }

    public UserAchievement(User user, Achievement achievement, Boolean achieved) {
        this.user = user;
        this.achievement = achievement;
        this.achieved = achieved;
        this.id = new UserAchievementId(user.getId(), achievement.getId());
    }

    public UserAchievementId getId() {
        return id;
    }

    public void setId(UserAchievementId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public Boolean getAchieved() {
        return achieved;
    }

    public void setAchieved(Boolean achieved) {
        this.achieved = achieved;
    }
}
