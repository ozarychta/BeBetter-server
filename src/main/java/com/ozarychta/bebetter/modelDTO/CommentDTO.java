package com.ozarychta.bebetter.modelDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ozarychta.bebetter.model.Comment;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CommentDTO {

    private Long id;

    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")
    private OffsetDateTime createdAt;

    private Long creatorId;

    private String creatorUsername;

    public CommentDTO(Comment c) {
        id = c.getId();
        text = c.getText();
        createdAt = c.getCreatedAt().atOffset(ZoneOffset.UTC);
        creatorUsername = c.getCreator().getUsername();
        creatorId = c.getCreator().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }
}

