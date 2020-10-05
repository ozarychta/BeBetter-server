package com.ozarychta.bebetter.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserAchievementId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "achievement_id")
    private Long achievementId;

    public UserAchievementId() {
    }

    public UserAchievementId(Long userId, Long achievementId) {
        this.userId = userId;
        this.achievementId = achievementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAchievementId that = (UserAchievementId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(achievementId, that.achievementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementId);
    }
}
