package com.ozarychta.bebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_achievements")
@Data
@NoArgsConstructor
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

    public UserAchievement(User user, Achievement achievement, Boolean achieved) {
        this.user = user;
        this.achievement = achievement;
        this.achieved = achieved;
        this.id = new UserAchievementId(user.getId(), achievement.getId());
    }
}
