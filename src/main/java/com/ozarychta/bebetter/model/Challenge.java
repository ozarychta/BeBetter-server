package com.ozarychta.bebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.enums.AccessType;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges", uniqueConstraints={@UniqueConstraint(
        columnNames = {"title" , "description", "city", "confirmation_type", "start_date", "end_date", "category",
                "repeat_period", "access_type", "challenge_state", "goal"})})
@Data
@NoArgsConstructor
public class Challenge implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    private User creator;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "access_type")
    private AccessType accessType;

    @Column(name = "category")
    private Category category;

    @Column(name = "repeat_period")
    private RepeatPeriod repeatPeriod;

    @Column(name = "city")
    private String city;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "challenge_state")
    private ChallengeState challengeState;

    @Column(name = "confirmation_type")
    private ConfirmationType confirmationType;

    @Column(name = "goal")
    private Integer goal;

    @JsonIgnore
    @OneToMany(mappedBy="challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy="challenges")
    private List<User> participants = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
