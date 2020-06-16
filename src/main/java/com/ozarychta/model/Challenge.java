package com.ozarychta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ozarychta.enums.*;
import com.ozarychta.enums.AccessType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "challenges", uniqueConstraints={@UniqueConstraint(
        columnNames = {"title" , "description", "city", "confirmation_type", "start_date", "end_date", "category",
                "repeat_period", "access_type", "challenge_state", "goal", "is_more_better" })})
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
    private com.ozarychta.enums.AccessType accessType;

    @Column(name = "category")
    private Category category;

    @Column(name = "repeat_period")
    private RepeatPeriod repeatPeriod;

    @Column(name = "city")
    private String city;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "challenge_state")
    private ChallengeState challengeState;

    @Column(name = "confirmation_type")
    private ConfirmationType confirmationType;

    @Column(name = "goal")
    private Integer goal;

    @Column(name = "is_more_better")
    private Boolean isMoreBetter;

    @JsonIgnore
    @OneToMany(mappedBy="challenge", cascade = {CascadeType.ALL})
    private List<Day> days = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy="challenges")
    private List<User> participants = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy="challenge")
    private List<Comment> comments = new ArrayList<>();

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
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

    public com.ozarychta.enums.AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RepeatPeriod getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ChallengeState getChallengeState() {
        return challengeState;
    }

    public void setChallengeState(ChallengeState challengeState) {
        this.challengeState = challengeState;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Boolean getMoreBetter() {
        return isMoreBetter;
    }

    public void setMoreBetter(Boolean moreBetter) {
        isMoreBetter = moreBetter;
    }
}
