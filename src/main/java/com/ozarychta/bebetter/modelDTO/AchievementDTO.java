package com.ozarychta.bebetter.modelDTO;

import com.ozarychta.bebetter.model.Achievement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AchievementDTO {

    private Long id;

    private String title;

    private String description;

    private Boolean achieved;

    public AchievementDTO(Achievement a, Boolean achieved) {
        this.id = a.getId();
        this.title = a.getTitle();
        this.description = a.getDescription();
        this.achieved = achieved;
    }
}
