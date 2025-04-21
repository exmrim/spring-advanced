package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.response.UserSaveResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    AuthService authService;

    @Test
    public void signup_성공() {

        //given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "test", "USER");

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(false);

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = new User(
            signupRequest.getEmail(),
            encodedPassword,
            userRole
        );
        ReflectionTestUtils.setField(newUser, "id", 1L);

        given(userRepository.save(any())).willReturn(newUser);
        String bearerToken = jwtUtil.createToken(newUser.getId(), newUser.getEmail(), userRole);

        // when
        SignupResponse signupResponse = authService.signup(signupRequest);

        // then
        assertNotNull(signupResponse);

    }

    @Test
    public void signup_이미_가입된_사용자_예외처리() {

        //given
        SignupRequest signupRequest = new SignupRequest("test@test.com", "test", "USER");

        given(userRepository.existsByEmail(signupRequest.getEmail())).willReturn(true);

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signup(signupRequest);
        });

        //then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());

    }

    @Test
    public void signin_성공() {

        //given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "password");

        User user = new User("test@test.com","password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(true);

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        //when
        SigninResponse signinResponse = authService.signin(signinRequest);

        // then
        assertNotNull(signinResponse);
    }

    @Test
    public void signin_가입되지_않은_유저_예외() {
        //given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "password");

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signin(signinRequest);
        });

        //then
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    public void signin_잘못된_비밀번호_예외() {
        //given
        SigninRequest signinRequest = new SigninRequest("test@test.com", "password");
        User user = new User("test@test.com","password1", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(signinRequest.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())).willReturn(false);

        //when
        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.signin(signinRequest);
        });

        //then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());

    }

}