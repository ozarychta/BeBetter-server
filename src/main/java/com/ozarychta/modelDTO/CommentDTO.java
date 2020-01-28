package com.ozarychta.modelDTO;

import com.ozarychta.model.Comment;

import java.util.Date;

public class CommentDTO {

    private Long id;

    private String text;

    private Date createdAt;

    private String username;

    public CommentDTO(Comment c) {
        id = c.getId();
        text = c.getText();
        createdAt = c.getCreatedAt();
        username = c.getCreator().getUsername();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

