package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    private CommentMapper() {
    }

    public static CommentDto toCommentDto(Comment comment) {
        long id = comment.getId();
        String text = comment.getText();
        String authorName = comment.getAuthor().getName();
        LocalDateTime created = comment.getCreated();

        return new CommentDto(id, text, authorName, created);
    }

    public static Comment toComment(CommentDto commentDto) {
        long id = commentDto.getId();
        String text = commentDto.getText();

        return new Comment(id, text, null, null, null);
    }
}