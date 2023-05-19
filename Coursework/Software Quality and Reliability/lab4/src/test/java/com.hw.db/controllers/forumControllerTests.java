package com.hw.db.controllers;

import com.hw.db.DAO.ForumDAO;
import com.hw.db.DAO.UserDAO;
import com.hw.db.models.Forum;
import com.hw.db.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class forumControllerTests {
    private User loggedIn;
    private Forum toCreate;

    @BeforeEach
    @DisplayName("forum creation test")
    void createForumTest() {
        loggedIn = new User("some", "some@email.mu", "name", "nothing");
        toCreate = new Forum(12, "some", 3, "title", "some");
    }

    @Test
    @DisplayName("Correct forum creation test")
    void correctlyCreatesForum() {
        try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
            userMock.when(() -> UserDAO.Search("some"))
                    .thenReturn(loggedIn);
            try (MockedStatic forumDAO = Mockito.mockStatic(ForumDAO.class)) {
                forumController controller = new forumController();
                controller.create(toCreate);
                assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(toCreate), controller.create(toCreate), "Result for succeeding forum creation");
            }
            assertEquals(loggedIn, UserDAO.Search("some"));
        }
    }

    @Test
    @DisplayName("Not authorithed user fails to create forum")
    void noLoginWhenCreatesForum() {
        try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
            userMock.when(() -> UserDAO.Search("some"))
                    .thenThrow(new EmptyResultDataAccessException("User not found.", 0));
            forumController controller = new forumController();
            controller.create(toCreate);
            assertEquals(
                    HttpStatus.NOT_FOUND,
                    controller.create(toCreate).getStatusCode(),
                    "Result when unauthorised user tried to create a forum");
        }
    }

    @Test
    @DisplayName("User fails to create forum because it already exists")
    void conflictExistsWhenCreatesForum() {
        try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
            userMock.when(() -> UserDAO.Search("some"))
                    .thenReturn(loggedIn);
            try (MockedStatic forumDAO = Mockito.mockStatic(ForumDAO.class)) {
                forumDAO.when(() -> ForumDAO.CreateForum(toCreate)).thenThrow(new DuplicateKeyException("Forum already exists"));
                forumDAO.when(() -> ForumDAO.Info(toCreate.getSlug())).thenReturn(toCreate);
                forumController controller = new forumController();
                controller.create(toCreate);
                assertEquals(
                        ResponseEntity.status(HttpStatus.CONFLICT).body(toCreate),
                        controller.create(toCreate),
                        "Result for conflict while forum creation"
                );
            }
            assertEquals(loggedIn, UserDAO.Search("some"));
        }
    }

    @Test
    @DisplayName("User fails to create forum because server cannot access DB")
    void conflictDbAccessWhenCreatesForum() {
        try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
            userMock.when(() -> UserDAO.Search("some"))
                    .thenReturn(loggedIn);
            try (MockedStatic forumDAO = Mockito.mockStatic(ForumDAO.class)) {
                forumDAO.when(() -> ForumDAO.CreateForum(toCreate)).thenThrow(new DataAccessException("Access error") {
                });
                forumController controller = new forumController();
                controller.create(toCreate);
                assertEquals(
                        HttpStatus.NOT_ACCEPTABLE,
                        controller.create(toCreate).getStatusCode(),
                        "Result for DB error while server creation");
            }
            assertEquals(loggedIn, UserDAO.Search("some"));
        }
    }
}
