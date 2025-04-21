package org.example.expert.domain.user.controller;

import org.example.expert.domain.MockAuthUserArgumentResolver;
import org.example.expert.domain.comment.controller.CommentController;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() {
        UserController userController = new UserController(userService);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new MockAuthUserArgumentResolver())
                .build();
    }

    @Test
    void getUser_성공() {

    }

    @Test
    void changePassword() throws Exception {
        AuthUser user = new AuthUser(1L, "test@test.com", UserRole.USER);

        // when & then
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "oldPassword": "oldPassword",
                "newPassword": "newPassword!"
            }
        """))
                .andExpect(status().isOk());

        // verify
        verify(userService).changePassword(eq(user.getId()), any(UserChangePasswordRequest.class));
    }
}