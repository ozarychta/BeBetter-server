package com.ozarychta.bebetter.modelDTO;

import com.ozarychta.bebetter.model.Comment;

import java.util.Date;

public class CommentDTO {

    private Long id;

    private String text;

    private Date createdAt;

    private Long creatorId;

    private String creatorUsername;

    public CommentDTO(Comment c) {
        id = c.getId();
        text = c.getText();
        createdAt = c.getCreatedAt();
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
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

