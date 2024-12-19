package com.hw.db.controllers;

import com.hw.db.models.Forum;
import com.hw.db.models.User;
import com.hw.db.DAO.ForumDAO;
import com.hw.db.DAO.UserDAO;
import com.hw.db.controllers.ForumController;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ForumControllerTests {
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
            try (MockedStatic<ForumDAO> forumDAO = Mockito.mockStatic(ForumDAO.class)) {
                ForumController controller = new ForumController();
                controller.create(toCreate);
                assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(toCreate), controller.create(toCreate),
                        "Result for succeeding forum creation");
            }
            assertEquals(loggedIn, UserDAO.Search("some"));
        }
    }
}