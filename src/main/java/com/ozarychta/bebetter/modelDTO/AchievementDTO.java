package com.ozarychta.bebetter.modelDTO;

import com.ozarychta.bebetter.model.Achievement;

public class AchievementDTO {

    private Long id;

    private String title;

    private String description;

    private Boolean achieved;

    public AchievementDTO() {
    }

    public AchievementDTO(Achievement a, Boolean achieved) {
        this.id = a.getId();
        this.title = a.getTitle();
        this.description = a.getDescription();
        this.achieved = achieved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAchieved() {
        return achieved;
    }

    public void setAchieved(Boolean achieved) {
        this.achieved = achieved;
    }
}
