package com.ozarychta.bebetter.modelDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ozarychta.bebetter.model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
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
}

