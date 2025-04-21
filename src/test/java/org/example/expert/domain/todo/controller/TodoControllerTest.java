package org.example.expert.domain.todo.controller;

import org.example.expert.domain.MockAuthUserArgumentResolver;
import org.example.expert.domain.comment.controller.CommentController;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TodoService todoService;

    @BeforeEach
    void setup() {
        TodoController todoController = new TodoController(todoService);

        mockMvc = MockMvcBuilders.standaloneSetup(todoController)
                .setCustomArgumentResolvers(new MockAuthUserArgumentResolver())
                .build();
    }

    @Test
    void saveTodo() throws Exception {
        // given
        TodoSaveResponse todoSaveResponse = new TodoSaveResponse(1L, "title", "content", "sunny", new UserResponse(1L, "test@test.com"));

        given(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class)))
                .willReturn(todoSaveResponse);

        // when & then
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "title": "title",
                "contents" : "content"
            }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.contents").value("content"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("test@test.com"));
    }

    @Test
    void getTodos() throws Exception {
        // given
        List<TodoResponse> todoResponses = List.of(
                new TodoResponse(1L, "title1", "content1", "sunny", new UserResponse(1L, "test@test.com"), LocalDateTime.now(), LocalDateTime.now()),
                new TodoResponse(2L, "title2", "content2", "windy", new UserResponse(1L, "test@test.com"), LocalDateTime.now(), LocalDateTime.now())
        );

        Page<TodoResponse> pageResponse = new PageImpl<>(todoResponses, PageRequest.of(0, 10), 2);

        given(todoService.getTodos(eq(1), eq(10))).willReturn(pageResponse);

        mockMvc.perform(get("/todos")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("title1"))
                .andExpect(jsonPath("$.content[0].contents").value("content1"))
                .andExpect(jsonPath("$.content[0].weather").value("sunny"))
                .andExpect(jsonPath("$.content[0].user.id").value(1L))
                .andExpect(jsonPath("$.content[0].user.email").value("test@test.com"))
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.content[0].modifiedAt").exists())
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].title").value("title2"))
                .andExpect(jsonPath("$.content[1].contents").value("content2"))
                .andExpect(jsonPath("$.content[1].weather").value("windy"))
                .andExpect(jsonPath("$.content[1].user.id").value(1L))
                .andExpect(jsonPath("$.content[1].user.email").value("test@test.com"))
                .andExpect(jsonPath("$.content[1].createdAt").exists())
                .andExpect(jsonPath("$.content[1].modifiedAt").exists());
    }

    @Test
    void getTodo() throws Exception {
        //given
        long todoId = 1L;

        TodoResponse todoResponse = new TodoResponse(1L, "title1", "content1", "sunny", new UserResponse(1L, "test@test.com"), LocalDateTime.now(), LocalDateTime.now());

        given(todoService.getTodo(todoId)).willReturn(todoResponse);

        // when & then
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.contents").value("content1"))
                .andExpect(jsonPath("$.weather").value("sunny"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@test.com"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.modifiedAt").exists());
    }
}