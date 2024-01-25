package com.xeridia.processor;

import com.xeridia.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CommentProcessor implements ItemProcessor<Comment, Comment> {

    @Override
    public Comment process(Comment comment) throws Exception {
        log.info("Processing comment with id {}", comment.id());
        return comment;
    }
}
