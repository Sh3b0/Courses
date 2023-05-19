package com.hw.db.controllers;

import com.hw.db.DAO.ThreadDAO;
import com.hw.db.DAO.UserDAO;
import com.hw.db.models.Post;
import com.hw.db.models.Thread;
import com.hw.db.models.User;
import com.hw.db.models.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ThreadControllerTests {
    private ThreadController controller;
    private User user;
    private String username = "testUser";
    private Thread thread;
    private int threadId = 42;
    private String slug = "exampleSlug";
    private Post post;

    @BeforeEach
    void setup() {
        final Timestamp ts = Timestamp.valueOf("2021-02-07 00:00:00");
        final String forum = "theForum";
        user = new User(username, "user@example.com", "name", "Hello World");
        post = new Post(username, ts, forum, "post message", 0, 0, false);
        controller = new ThreadController();
        thread = new Thread(username, ts, forum, "thread message", slug, "Thread Title", 3);
        thread.setId(threadId);
    }

    @Test
    @DisplayName("Get thread by slug and ID")
    void checkSlugAndId() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            // Via slug
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
            assertEquals(controller.CheckIdOrSlug(slug), thread);
            assertNull(controller.CheckIdOrSlug("unknownSlug"));

            // Via ID
            threadMock.when(() -> ThreadDAO.getThreadById(threadId)).thenReturn(thread);
            assertEquals(controller.CheckIdOrSlug(Integer.toString(threadId)), thread);
            assertNull(controller.CheckIdOrSlug("1337"));
        }
    }

    @Test
    @DisplayName("Create a post")
    void createPost() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
                threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
                userMock.when(() -> UserDAO.Info(username)).thenReturn(user);
                final List<Post> postList = List.of(post);

                assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(postList),
                        controller.createPost(slug, postList));
                assertEquals(thread, ThreadDAO.getThreadBySlug(slug));
            }
        }
    }

    @Test
    @DisplayName("Get posts")
    void PostsTest() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);

            final List<Post> postList = List.of(post);

            threadMock.when(() -> ThreadDAO.getPosts(threadId, 1, 100, "flat", false)).thenReturn(postList);

            // Empty list of posts
            threadMock.when(() -> ThreadDAO.getPosts(threadId, 0, 100, "flat", false))
                    .thenReturn(Collections.emptyList());

            assertEquals(ResponseEntity.status(HttpStatus.OK).body(postList),
                    controller.Posts(slug, 1, 100, "flat", false));

            assertEquals(ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList()),
                    controller.Posts(slug, 0, 100, "flat", false));
        }
    }

    @Test
    @DisplayName("Change thread")
    void changeThread() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            final String slug2 = "anotherSlug";
            final Timestamp ts2 = Timestamp.valueOf("2022-02-21 19:45:00");
            final int threadId2 = 871999;
            final Thread thread2 = new Thread(username, ts2, "something", "some_message", slug2, "Title", 4);
            thread2.setId(threadId2);
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug2)).thenReturn(thread2);
            threadMock.when(() -> ThreadDAO.getThreadById(threadId2)).thenReturn(thread2);

            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);

            threadMock.when(() -> ThreadDAO.change(thread, thread2)).thenAnswer((invocationOnMock) -> {
                threadMock.when(() -> ThreadDAO.getThreadBySlug(slug2)).thenReturn(thread2);
                return null;
            });

            assertEquals(ResponseEntity.status(HttpStatus.OK).body(thread2),
                    controller.change(slug2, thread2));
            assertEquals(thread2, controller.CheckIdOrSlug(slug2));
        }
    }

    @Test
    @DisplayName("Get thread details")
    void threadInfo() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
            assertEquals(ResponseEntity.status(HttpStatus.OK).body(thread), controller.info(slug));

            // Non-existing thread
            threadMock.when(() -> ThreadDAO.getThreadBySlug("unknownSlug")).thenThrow(new DataAccessException("") {
            });
            assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null).getStatusCode(),
                    controller.info("unknownSlug").getStatusCode());
        }
    }

    @Test
    @DisplayName("Create Vote")
    void createVote() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
                userMock.when(() -> UserDAO.Info(username)).thenReturn(user);
                final Vote vote = new Vote(username, 1);
                threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
                threadMock.when(() -> ThreadDAO.change(vote, thread.getVotes())).thenReturn(thread.getVotes());
                assertEquals(ResponseEntity.status(HttpStatus.OK).body(thread),
                        controller.createVote(slug, vote));
            }
        }
    }
}
