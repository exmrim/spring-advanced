package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private WeatherClient weatherClient;
    @InjectMocks
    private TodoService todoService;

    @Test
    void saveTodo_정상_등록() {
        //given
        AuthUser auth = new AuthUser(1L, "test@test.com", UserRole.USER);
        User user = User.fromAuthUser(auth);
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");

        String weather = "sunny";

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );

        given(weatherClient.getTodayWeather()).willReturn(weather);
        given(todoRepository.save(any())).willReturn(newTodo);

        //when
        TodoSaveResponse result = todoService.saveTodo(auth, todoSaveRequest);

        //then
        assertNotNull(result);
    }

    @Test
    void getTodos_정상_조회() {
        //given
        AuthUser auth = new AuthUser(1L, "test@test.com", UserRole.USER);
        User user = User.fromAuthUser(auth);

        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);

        List<Todo> todos = List.of(
                new Todo("title1", "contents1", "sunny", user),
                new Todo("title2", "contents2", "cloudy", user)
        );

        Page<Todo> todosPage = new PageImpl<>(todos, pageable, todos.size());

        given(todoRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(todosPage);

        //when
        Page<TodoResponse> result = todoService.getTodos(page, size);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals("title1", result.getContent().get(0).getTitle());
        assertEquals("contents1", result.getContent().get(0).getContents());
        assertEquals("title2", result.getContent().get(1).getTitle());
        assertEquals("contents2", result.getContent().get(1).getContents());
    }

    @Test
    void getTodo_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
        //given
        long todoId = 1L;
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.empty());

        //when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,  () -> todoService.getTodo(todoId));

        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    void getTodo_정상_조회() {
        //given
        long todoId = 1L;
        Todo todo = new Todo("title1", "contents1", "sunny",
                new User("test@test.com", "password", UserRole.USER));
        ReflectionTestUtils.setField(todo, "id", todoId);

        User user = new User(todo.getUser().getEmail(), todo.getUser().getPassword(), todo.getUser().getUserRole());

        //when
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));
        TodoResponse todoResponse = todoService.getTodo(todoId);

        // then
        assertEquals(1L, todoResponse.getId());
        assertEquals("title1", todoResponse.getTitle());
        assertEquals("contents1", todoResponse.getContents());
        assertEquals("sunny", todoResponse.getWeather());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("password", user.getPassword());
    }



}