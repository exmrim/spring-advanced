package org.example.expert.domain.comment.controller;

import org.example.expert.domain.MockAuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;

    @BeforeEach
    void setup() {
        CommentController commentController = new CommentController(commentService);

        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(new MockAuthUserArgumentResolver())
                .build();
    }

    @Test
    void saveComment() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        long todoId = 1L;

        CommentSaveResponse commentSaveResponse = new CommentSaveResponse(1L, "comments", new UserResponse(authUser.getId(), authUser.getEmail()));

        given(commentService.saveComment(any(AuthUser.class), eq(todoId), any(CommentSaveRequest.class)))
                .willReturn(commentSaveResponse);

        // when & then
        mockMvc.perform(post("/todos/{todoId}/comments", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "contents": "contents"
            }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.contents").value("comments"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@test.com"));
    }

    @Test
    void getComments() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        long todoId = 1L;

        List<CommentResponse> commentResponses = List.of(
                new CommentResponse(1L, "comments1", new UserResponse(1L, "test@test.com")),
                new CommentResponse(2L, "comments2", new UserResponse(1L, "test@test.com"))
        );

        given(commentService.getComments(eq(todoId))).willReturn(commentResponses);

        // when & then
        mockMvc.perform(get("/todos/{todoId}/comments", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].contents").value("comments1"))
                .andExpect(jsonPath("$[0].user.id").value(1L))
                .andExpect(jsonPath("$[0].user.email").value("test@test.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].contents").value("comments2"))
                .andExpect(jsonPath("$[1].user.id").value(1L))
                .andExpect(jsonPath("$[1].user.email").value("test@test.com"));
    }
}