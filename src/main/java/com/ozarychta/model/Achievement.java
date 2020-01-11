package com.ozarychta.model;

import com.ozarychta.enums.RequirementType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "achievements")
public class Achievement implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private RequirementType requirementType;

    private Integer requirementValue;

    private Integer reward;

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

    public RequirementType getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(RequirementType requirementType) {
        this.requirementType = requirementType;
    }

    public Integer getRequirementValue() {
        return requirementValue;
    }

    public void setRequirementValue(Integer requirementValue) {
        this.requirementValue = requirementValue;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }
}
