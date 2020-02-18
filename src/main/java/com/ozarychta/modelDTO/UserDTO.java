package com.ozarychta.modelDTO;

import com.ozarychta.model.User;

public class UserDTO {

    private Long id;

    private String username;

    private Integer rankingPoints;

    private Integer highestStreak;

    private String aboutMe;

    private String mainGoal;

    public UserDTO(User u) {
        id = u.getId();
        username = u.getUsername();
        rankingPoints = u.getRankingPoints();
        highestStreak = u.getHighestStreak();
        aboutMe = u.getAboutMe();
        mainGoal = u.getMainGoal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
