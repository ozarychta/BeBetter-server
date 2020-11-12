package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.Achievement;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.model.UserAchievement;
import com.ozarychta.bebetter.dto.AchievementDTO;
import com.ozarychta.bebetter.repository.AchievementRepository;
import com.ozarychta.bebetter.repository.UserAchievementRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultAchievementService implements AchievementService {

    private final AchievementRepository achievementRepository;

    private final UserAchievementRepository userAchievementRepository;

    private final UserRepository userRepository;

    @Override
    public List<Achievement> getAchievements() {
        return achievementRepository.findAll();
    }

    @Override
    public Achievement getAchievement(Long achievementId) {
        return achievementRepository.findById(achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement with id " + achievementId + " not found"));
    }

    @Override
    public Achievement saveAchievement(Achievement achievement) {
        Achievement a = achievementRepository.save(achievement);

        List<User> users = userRepository.findAll();

        for (User u : users) {
            UserAchievement ua = new UserAchievement(u, a, false);
            userAchievementRepository.save(ua);
        }
        return a;
    }

    @Override
    public List<AchievementDTO> getAchievementsByUserId(Long userId) {
        return userAchievementRepository.findByUserId(userId).stream().map(a -> new AchievementDTO(a.getAchievement(), a.getAchieved())).collect(Collectors.toList());
    }

    @Override
    public AchievementDTO getUserAchievement(Long userAchievementId) {
        UserAchievement ua = userAchievementRepository.findById(userAchievementId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAchievement with id " + userAchievementId + " not found"));
        return new AchievementDTO(ua.getAchievement(), ua.getAchieved());
    }
}
