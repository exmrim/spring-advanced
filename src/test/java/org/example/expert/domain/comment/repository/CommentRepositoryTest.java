package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ManagerRepository managerRepository;

    @Test
    void findByTodoIdWithUser() {
        // Given
        long todoId = 1L;

        // When
        User user = userRepository.save(new User("test@test.com", "password", UserRole.USER));

        Todo todo = new Todo("title1", "contents1", "sunny", user);
        todoRepository.save(todo);

        Manager manager = new Manager(user, todo);
        managerRepository.save(manager);

        Comment comment = new Comment("comment", user, todo);
        commentRepository.save(comment);

        // when
        List<Comment> comments = commentRepository.findByTodoIdWithUser(todoId);

        // then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContents()).isEqualTo("comment");
        assertThat(comments.get(0).getUser().getEmail()).isEqualTo("test@test.com");
    }
}