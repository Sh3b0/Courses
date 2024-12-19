package com.hw.db.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.hw.db.DAO.ForumDAO;
import com.hw.db.DAO.ThreadDAO;
import com.hw.db.DAO.UserDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

public class forumDAOTests {

    JdbcTemplate mockJdbc;
    ForumDAO forum;
    String threadDate = "12.02.2019";
    String threadSlug = "slug";
    int threadLimit = 10;
    UserDAO.UserMapper USER_MAPPER;

    @BeforeEach
    void setupThreadList() {
        mockJdbc = mock(JdbcTemplate.class);
        forum = new ForumDAO(mockJdbc);
        USER_MAPPER = new UserDAO.UserMapper();
    }

    @Test
    void UserListTest1() {
        ForumDAO.UserList(threadSlug, threadLimit, threadDate, false);
        String q = "SELECT nickname,fullname,email,about FROM forum_users WHERE forum = (?)::citext AND  nickname > (?)::citext ORDER BY nickname LIMIT ?;";
        verify(mockJdbc).query(Mockito.eq(q), Mockito.any(Object[].class), Mockito.any(UserDAO.UserMapper.class));
    }

    @Test
    void UserListTest2() {
        ForumDAO.UserList(threadSlug, null, null, null);
        String q = "SELECT nickname,fullname,email,about FROM forum_users WHERE forum = (?)::citext ORDER BY nickname;";
        verify(mockJdbc).query(Mockito.eq(q), Mockito.any(Object[].class), Mockito.any(UserDAO.UserMapper.class));
    }

    @Test
    void UserListTest3() {
        ForumDAO.UserList(threadSlug, threadLimit, threadDate, true);
        String q = "SELECT nickname,fullname,email,about FROM forum_users WHERE forum = (?)::citext AND  nickname < (?)::citext ORDER BY nickname desc LIMIT ?;";
        verify(mockJdbc).query(Mockito.eq(q), Mockito.any(Object[].class), Mockito.any(UserDAO.UserMapper.class));
    }

    @Test
    void ThreadListTest1() {
        ForumDAO.ThreadList(threadSlug, null, null, null);
        verify(mockJdbc).query(Mockito.eq("SELECT * FROM threads WHERE forum = (?)::CITEXT ORDER BY created;"),
                Mockito.any(Object[].class), Mockito.any(ThreadDAO.ThreadMapper.class));
    }

    @Test
    void ThreadListTest2() {
        ForumDAO.ThreadList(threadSlug, null, threadDate, false);
        verify(mockJdbc).query(Mockito.eq(
                "SELECT * FROM threads WHERE forum = (?)::CITEXT AND created>=(?)::TIMESTAMP WITH TIME ZONE ORDER BY created;"),
                Mockito.any(Object[].class), Mockito.any(ThreadDAO.ThreadMapper.class));
    }

    @Test
    void ThreadListTest3() {
        ForumDAO.ThreadList(threadSlug, null, threadDate, true);
        verify(mockJdbc).query(Mockito.eq(
                "SELECT * FROM threads WHERE forum = (?)::CITEXT AND created<=(?)::TIMESTAMP WITH TIME ZONE ORDER BY created desc;"),
                Mockito.any(Object[].class), Mockito.any(ThreadDAO.ThreadMapper.class));
    }

    @Test
    void ThreadListTest4() {
        ForumDAO.ThreadList(threadSlug, threadLimit, threadDate, true);
        verify(mockJdbc).query(Mockito.eq(
                "SELECT * FROM threads WHERE forum = (?)::CITEXT AND created<=(?)::TIMESTAMP WITH TIME ZONE ORDER BY created desc LIMIT ?;"),
                Mockito.any(Object[].class), Mockito.any(ThreadDAO.ThreadMapper.class));
    }

    @Test
    void ThreadListTest5() {
        ForumDAO.ThreadList(threadSlug, threadLimit, threadDate, false);
        verify(mockJdbc).query(Mockito.eq(
                "SELECT * FROM threads WHERE forum = (?)::CITEXT AND created>=(?)::TIMESTAMP WITH TIME ZONE ORDER BY created LIMIT ?;"),
                Mockito.any(Object[].class), Mockito.any(ThreadDAO.ThreadMapper.class));
    }

    @Test
    void ThreadListTest6() {
        ForumDAO.ThreadList(threadSlug, threadLimit, null, true);
        verify(mockJdbc).query(
                Mockito.eq("SELECT * FROM threads WHERE forum = (?)::CITEXT ORDER BY created desc LIMIT ?;"),
                Mockito.any(Object[].class), Mockito.any(ThreadDAO.ThreadMapper.class));
    }

}
