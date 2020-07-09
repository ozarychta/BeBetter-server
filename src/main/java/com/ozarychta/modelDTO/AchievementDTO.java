package com.ozarychta.modelDTO;

import com.ozarychta.model.Achievement;

public class AchievementDTO {

    private Long id;

    private String title;

    private String description;

    public AchievementDTO() {
    }

    public AchievementDTO(Achievement a) {
        this.id = a.getId();
        this.title = a.getTitle();
        this.description = a.getDescription();
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
}
