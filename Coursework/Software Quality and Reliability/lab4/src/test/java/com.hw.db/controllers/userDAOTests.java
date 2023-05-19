package com.hw.db.controllers;

import com.hw.db.DAO.UserDAO;
import com.hw.db.models.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class userDAOTests {

    JdbcTemplate mockJdbc;
    UserDAO mockUser;

    @BeforeEach
    void setupUserChange() {
        mockJdbc = mock(JdbcTemplate.class);
        mockUser = new UserDAO(mockJdbc);

    }

    @Test
    void UserChangeTest1() {
        User user = new User("nickname", null, null, null);
        UserDAO.Change(user);
        verifyNoInteractions(mockJdbc);
    }

    @Test
    void UserChangeTest2() {
        User user = new User("nickname", "email", "full name", "about");
        UserDAO.Change(user);
        String q = "UPDATE \"users\" SET  email=? , fullname=? , about=?  WHERE nickname=?::CITEXT;";
        verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    void UserChangeTest3() {
        User user = new User("nickname", "email", null, null);
        UserDAO.Change(user);
        String q = "UPDATE \"users\" SET  email=?  WHERE nickname=?::CITEXT;";
        verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void UserChangeTest4() {
        User user = new User("nickname", null, "full name", null);
        UserDAO.Change(user);
        String q = "UPDATE \"users\" SET  fullname=?  WHERE nickname=?::CITEXT;";
        verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void UserChangeTest5() {
        User user = new User("nickname", "email", null, "about");
        UserDAO.Change(user);
        String q = "UPDATE \"users\" SET  email=? , about=?  WHERE nickname=?::CITEXT;";
        verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void UserChangeTest6() {
        User user = new User("nickname", null, null, "about");
        UserDAO.Change(user);
        String q = "UPDATE \"users\" SET  about=?  WHERE nickname=?::CITEXT;";
        verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void UserChangeTest7() {
        User user = new User("nickname", null, "full name", "about");
        UserDAO.Change(user);
        String q = "UPDATE \"users\" SET  fullname=? , about=?  WHERE nickname=?::CITEXT;";
        verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void UserChangeTest8() {
        User user = new User(null, null, null, null);
        UserDAO.Change(user);
        verifyNoInteractions(mockJdbc);
    }
}
