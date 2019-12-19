package com.ozarychta.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_achievements")
public class UserAchievement implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Integer currentStatus;

    private Boolean achieved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getAchieved() {
        return achieved;
    }

    public void setAchieved(Boolean achieved) {
        this.achieved = achieved;
    }
}
