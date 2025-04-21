package org.example.expert.domain.auth.controller;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.example.expert.domain.user.controller.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void signup() throws Exception {
        //given
        SignupResponse signupResponse = new SignupResponse("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0MUB0ZXN0LmNvbSIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc0NDg3OTg4MywiaWF0IjoxNzQ0ODc2MjgzfQ.LdReVcjb2dLkBVsmgORYgxX7li-Q4vfr6eYn_eup1PE");

        given(authService.signup(any(SignupRequest.class))).willReturn(signupResponse);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "email": "test@test.com",
                "password": "password",
                "userRole": "USER"
            }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0MUB0ZXN0LmNvbSIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc0NDg3OTg4MywiaWF0IjoxNzQ0ODc2MjgzfQ.LdReVcjb2dLkBVsmgORYgxX7li-Q4vfr6eYn_eup1PE"));
    }

    @Test
    void signin() throws Exception {
        //given
        SigninResponse signinResponse = new SigninResponse("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0MUB0ZXN0LmNvbSIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc0NDg3OTg4MywiaWF0IjoxNzQ0ODc2MjgzfQ.LdReVcjb2dLkBVsmgORYgxX7li-Q4vfr6eYn_eup1PE");

        given(authService.signin(any(SigninRequest.class))).willReturn(signinResponse);

        // when & then
        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "email": "test@test.com",
                "password": "password"
            }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJ0ZXN0MUB0ZXN0LmNvbSIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc0NDg3OTg4MywiaWF0IjoxNzQ0ODc2MjgzfQ.LdReVcjb2dLkBVsmgORYgxX7li-Q4vfr6eYn_eup1PE"));
    }
}