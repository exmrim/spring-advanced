package org.example.expert.domain.manager.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.MockAuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
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

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ManagerService managerService;
    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        ManagerController managerController = new ManagerController(managerService, jwtUtil);

        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                .setCustomArgumentResolvers(new MockAuthUserArgumentResolver())
                .build();
    }

    @Test
    void saveManager() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        long todoId = 1L;

        ManagerSaveResponse managerSaveResponse = new ManagerSaveResponse(1L, new UserResponse(authUser.getId(), authUser.getEmail()));

        given(managerService.saveManager(any(AuthUser.class), eq(todoId), any(ManagerSaveRequest.class)))
                .willReturn(managerSaveResponse);

        // when & then
        mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "managerUserId": 1
            }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@test.com"));

    }

    @Test
    void getMembers() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        long todoId = 1L;

        List<ManagerResponse> managerResponse = List.of(
                new ManagerResponse(1L, new UserResponse(1L, "test@test.com")),
                new ManagerResponse(2L, new UserResponse(1L, "test@test.com"))
        );

        given(managerService.getManagers(eq(todoId))).willReturn(managerResponse);

        // when & then
        mockMvc.perform(get("/todos/{todoId}/managers", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].user.id").value(1L))
                .andExpect(jsonPath("$[0].user.email").value("test@test.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].user.id").value(1L))
                .andExpect(jsonPath("$[1].user.email").value("test@test.com"));

    }


}