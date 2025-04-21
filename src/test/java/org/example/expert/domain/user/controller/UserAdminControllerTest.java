package org.example.expert.domain.user.controller;

import org.example.expert.domain.todo.controller.TodoController;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserAdminService userAdminService;

    @Test
    void changeUserRole() throws Exception {
        long userId = 1L;

        // when & then
        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .header("userRole", "USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "role": "USER"
            }
        """))
                .andExpect(status().isOk());

        // verify
        verify(userAdminService).changeUserRole(eq(userId), any(UserRoleChangeRequest.class));

    }
}