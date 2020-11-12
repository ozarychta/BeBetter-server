package com.ozarychta.bebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "days")
@Data
@NoArgsConstructor
public class Day implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private Boolean done;

    private Integer currentStatus;

    @JsonIgnore
    @ManyToOne
    private Challenge challenge;

    @JsonIgnore
    @ManyToOne
    private User user;

    private Integer streak;

    private Integer points;
}
