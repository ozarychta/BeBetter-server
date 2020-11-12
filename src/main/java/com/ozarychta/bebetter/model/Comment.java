package com.ozarychta.bebetter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Comment extends AuditingEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String text;

    @JsonIgnore
    @ManyToOne
    private Challenge challenge;

    @JsonIgnore
    @ManyToOne
    private User creator;
}
