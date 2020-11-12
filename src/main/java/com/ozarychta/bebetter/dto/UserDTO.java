package com.ozarychta.bebetter.dto;

import com.ozarychta.bebetter.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private Integer rankingPoints;

    private Integer highestStreak;

    private String aboutMe;

    private String mainGoal;

    private Boolean followed;

    public UserDTO(User u) {
        id = u.getId();
        username = u.getUsername();
        rankingPoints = u.getRankingPoints();
        highestStreak = u.getHighestStreak();
        aboutMe = u.getAboutMe();
        mainGoal = u.getMainGoal();
        followed = false;
    }
}
