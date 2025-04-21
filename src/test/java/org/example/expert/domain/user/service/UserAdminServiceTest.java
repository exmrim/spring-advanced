package org.example.expert.domain.user.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void userId_가_없을_경우_예외_처리() {
        //given
        long userId = 1L;
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("USER");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,  () -> userAdminService.changeUserRole(userId, userRoleChangeRequest));

        assertEquals("User not found", exception.getMessage());

    }

    @Test
    void changeUserRole_정상_수정() {
        long userId = 1L;
        UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("USER");

        User user = mock(User.class);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        //when
        userAdminService.changeUserRole(userId, userRoleChangeRequest);

        //then
        verify(user).updateRole(UserRole.USER);
    }
}