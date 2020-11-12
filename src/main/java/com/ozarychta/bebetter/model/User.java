package com.ozarychta.bebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints={@UniqueConstraint(columnNames = "google_user_id")})
@Data
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_user_id")
    private String googleUserId;

    private String username;

    private Integer rankingPoints;

    private Integer highestStreak;

    private String aboutMe;

    private String mainGoal;

    @JsonIgnore
    @OneToMany(mappedBy="creator")
    private List<Challenge> createdChallenges = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    private List<Challenge> challenges = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name="users_friends",
            joinColumns={@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="friend_id")})
    private List<User> followed = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "followed")
    private List<User> followers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="user")
    private List<Day> days = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="creator")
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAchievement> userAchievements = new ArrayList<>();
}
