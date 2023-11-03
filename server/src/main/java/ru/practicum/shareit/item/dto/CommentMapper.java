package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Comment;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        Long id = comment.getId();
        String text = comment.getText();
        String authorName = comment.getAuthor().getName();
        LocalDateTime created = comment.getCreated();

        return new CommentDto(id, text, authorName, created);
    }

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .build();
    }
}