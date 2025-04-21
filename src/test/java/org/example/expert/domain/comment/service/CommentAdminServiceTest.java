package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    void deleteComment_정상_삭제() {
        //given
        long commentId = 1L;

        //when
        commentAdminService.deleteComment(commentId);

        //then
        verify(commentRepository).deleteById(commentId);
    }
}