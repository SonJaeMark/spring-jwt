package com.github.sonjaemark.spring_jwt.todo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.github.sonjaemark.spring_jwt.user.User;
import com.github.sonjaemark.spring_jwt.user.UserRepository;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {

        String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
        .getName();

        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Todo createTask(String task) {

        User user = getCurrentUser();

        Todo todo = Todo.builder()
            .task(task)
            .createdAt(LocalDateTime.now())
            .user(user)
        .build();

        return todoRepository.save(todo);
    }

    public List<Todo> getAllTasks() {

        User user = getCurrentUser();

        return todoRepository.findByUser(user);
    }

    public Todo updateTask(Long id, String task) {

        User user = getCurrentUser();

        Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        todo.setTask(task);
        return todoRepository.save(todo);
    }

    public Todo markAsDone(Long id) {

        User user = getCurrentUser();

        Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        todo.setIsDone(true);

        return todoRepository.save(todo);
    }

    public void deleteTask(Long id) {

        User user = getCurrentUser();

        Todo todo = todoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        todoRepository.delete(todo);
    }
}