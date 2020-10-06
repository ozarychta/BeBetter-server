package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.model.Achievement;
import com.ozarychta.bebetter.model.UserAchievement;
import com.ozarychta.bebetter.modelDTO.AchievementDTO;

import java.util.List;

public interface AchievementService {

    List<Achievement> getAchievements();

    Achievement getAchievement(Long achievementId);

    Achievement saveAchievement(Achievement achievement);

    List<AchievementDTO> getAchievementsByUserId(Long userId);

    AchievementDTO getUserAchievement(Long userAchievementId);
}
