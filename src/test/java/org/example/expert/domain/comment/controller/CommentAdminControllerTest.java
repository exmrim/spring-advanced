package org.example.expert.domain.comment.controller;

import org.example.expert.domain.auth.controller.AuthController;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
class CommentAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentAdminService commentAdminService;

    @Test
    void deleteComment() throws Exception {
        //given
        long commentId = 1L;

        // when & then
        mockMvc.perform(delete("/admin/comments/{commentId}", commentId))
                .andExpect(status().isOk());

        // verify
        verify(commentAdminService, times(1)).deleteComment(commentId);
    }
}