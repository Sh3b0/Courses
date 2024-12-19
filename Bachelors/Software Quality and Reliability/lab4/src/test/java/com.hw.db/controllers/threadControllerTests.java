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

public class threadControllerTests {

    private User user;
    private String username = "testUser";

    private Thread thread;
    private threadController controller;

    private int threadId = 123;
    private String slug = "exampleSlug";

    private Post post;

    @BeforeEach
    void setup() {
        Timestamp ts = Timestamp.valueOf("2021-02-07 00:00:00");
        String forum = "theForum";
        user = new User(username, "some@email.mu", "name", "nothing");
        thread = new Thread(username, ts, forum, "thread message", slug, "Thread Title", 3);
        thread.setId(threadId);
        controller = new threadController();
        post = new Post(username, ts, forum, "post message", 0, 0, false);
    }

    @Test
    @DisplayName("Get by Slug")
    void checkSlug() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
            assertEquals(controller.CheckIdOrSlug(slug), thread);
            assertNull(controller.CheckIdOrSlug("unknownSlug"));
        }
    }

    @Test
    @DisplayName("Get by ID")
    void checkID() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            threadMock.when(() -> ThreadDAO.getThreadById(threadId)).thenReturn(thread);
            assertEquals(controller.CheckIdOrSlug(Integer.toString(threadId)), thread);
            assertNull(controller.CheckIdOrSlug("999"));
        }
    }

    @Test
    @DisplayName("Create Post")
    void creatPost() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            try (MockedStatic<UserDAO> userMock = Mockito.mockStatic(UserDAO.class)) {
                threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
                userMock.when(() -> UserDAO.Info(username)).thenReturn(user);
                List<Post> onePostList = new LinkedList<Post>();
                onePostList.add(post);

                assertEquals(ResponseEntity.status(HttpStatus.CREATED).body(onePostList),
                        controller.createPost(slug, onePostList));

                assertEquals(thread, ThreadDAO.getThreadBySlug(slug));
            }
        }
    }

    @Test
    @DisplayName("Get Posts")
    void PostsTest() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);

            List<Post> onePostList = new LinkedList<Post>();
            onePostList.add(post);

            threadMock.when(() -> ThreadDAO.getPosts(threadId, 1, 100, "flat", false)).thenReturn(onePostList);

            // Empty list of posts
            threadMock.when(() -> ThreadDAO.getPosts(threadId, 0, 100, "flat", false))
                    .thenReturn(Collections.emptyList());

            assertEquals(ResponseEntity.status(HttpStatus.OK).body(onePostList),
                    controller.Posts(slug, 1, 100, "flat", false));

            assertEquals(ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList()),
                    controller.Posts(slug, 0, 100, "flat", false));
        }
    }

    @Test
    @DisplayName("Thread Info")
    void threadInfo() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
            assertEquals(ResponseEntity.status(HttpStatus.OK).body(thread), controller.info(slug));
        }
    }

    @Test
    @DisplayName("Thread not found")
    void threadNotFound() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {
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
                Vote vote = new Vote(username, 1);
                threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);
                threadMock.when(() -> ThreadDAO.change(vote, thread.getVotes())).thenReturn(thread.getVotes());
                assertEquals(ResponseEntity.status(HttpStatus.OK).body(thread), controller.createVote(slug, vote));
            }
        }
    }

    @Test
    @DisplayName("Change Thread")
    void changeThread() {
        try (MockedStatic<ThreadDAO> threadMock = Mockito.mockStatic(ThreadDAO.class)) {

            String slug2 = "exampleSlug2";
            Timestamp ts2 = Timestamp.valueOf("2021-02-06 00:00:00");
            int threadId2 = 124;
            Thread thread2 = new Thread(username, ts2, "forum2", "message2", slug2, "Title 2", 4);
            thread2.setId(threadId2);
            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug2)).thenReturn(thread2);
            threadMock.when(() -> ThreadDAO.getThreadById(threadId2)).thenReturn(thread2);

            threadMock.when(() -> ThreadDAO.getThreadBySlug(slug)).thenReturn(thread);

            threadMock.when(() -> ThreadDAO.change(thread, thread2)).thenAnswer((invocationOnMock) -> {
                threadMock.when(() -> ThreadDAO.getThreadBySlug(slug2)).thenReturn(thread2);
                return null;
            });

            assertEquals(ResponseEntity.status(HttpStatus.OK).body(thread2), controller.change(slug2, thread2));
            assertEquals(thread2, controller.CheckIdOrSlug(slug2));
        }
    }
}