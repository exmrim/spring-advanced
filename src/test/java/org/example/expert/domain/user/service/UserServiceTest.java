package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void user_조회_실패() {
        //given
        long userId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.getUser(userId));

        //then
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUser_조회_성공() {
        //given
        long userId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        UserResponse userResponse = userService.getUser(userId);

        //then
        assertEquals(1L, userResponse.getId());
        assertEquals("test@test.com", userResponse.getEmail());
    }

    @Test
    void changePassword_중_기존_비밀번호와_일치_하면_안됨() {
        //given
        long userId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);

        UserChangePasswordRequest passwordChangeRequest = new UserChangePasswordRequest("password", "password");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(passwordChangeRequest.getNewPassword(), user.getPassword()))
                .willReturn(true);

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userService.changePassword(userId, passwordChangeRequest);
        });

        //then
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void changePassword_잘못된_비밀번호_예외() {
        //given
        long userId = 1L;

        User user = new User("test@test.com", "password", UserRole.USER);

        UserChangePasswordRequest passwordChangeRequest = new UserChangePasswordRequest("oldPassword", "newPassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(passwordChangeRequest.getNewPassword(), user.getPassword()))
                .willReturn(false);
        given(!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword()))
                .willReturn(false);

        //when
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            userService.changePassword(userId, passwordChangeRequest);
        });

        //then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void changePassword_변경_성공() {
        //given
        long userId = 1L;

        User user = mock(User.class);

        UserChangePasswordRequest passwordChangeRequest = new UserChangePasswordRequest("oldPassword", "newPassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(passwordChangeRequest.getNewPassword(), user.getPassword()))
                .willReturn(false);
        given(!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword()))
                .willReturn(true);

        userService.changePassword(userId, passwordChangeRequest);

        verify(user).changePassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));

    }
}