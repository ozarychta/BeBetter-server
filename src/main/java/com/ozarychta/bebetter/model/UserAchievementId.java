package com.ozarychta.bebetter.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class UserAchievementId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "achievement_id")
    private Long achievementId;

    public UserAchievementId(Long userId, Long achievementId) {
        this.userId = userId;
        this.achievementId = achievementId;
    }
}
