package com.hw.db.controllers;

import com.hw.db.DAO.PostDAO;
import com.hw.db.DAO.ThreadDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;

public class threadDAOTests {

    JdbcTemplate mockJdbc;
    ThreadDAO mockThread;
    int threadId = 44;
    int limit = 10;
    int since = 22;

    @BeforeEach
    void setupTreeSort() {
        mockJdbc = mock(JdbcTemplate.class);
        mockThread = new ThreadDAO(mockJdbc);
    }

    @Test
    void TreeSortTest1() {
        ThreadDAO.treeSort(threadId, limit, since, true);
        String q = "SELECT * FROM \"posts\" WHERE thread = ?  AND branch < (SELECT branch  FROM posts WHERE id = ?)  ORDER BY branch DESC  LIMIT ? ;";
        Mockito.verify(mockJdbc).query(Mockito.eq(q), Mockito.any(PostDAO.PostMapper.class), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void TreeSortTest2() {
        ThreadDAO.treeSort(threadId, null, since, false);
        String q = "SELECT * FROM \"posts\" WHERE thread = ?  AND branch > (SELECT branch  FROM posts WHERE id = ?)  ORDER BY branch;";
        Mockito.verify(mockJdbc).query(Mockito.eq(q), Mockito.any(PostDAO.PostMapper.class), Mockito.anyInt(),
                Mockito.anyInt());
    }

}
