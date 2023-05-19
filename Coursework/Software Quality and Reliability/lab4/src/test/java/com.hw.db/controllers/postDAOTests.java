package com.hw.db.controllers;

import com.hw.db.DAO.PostDAO;
import com.hw.db.models.Post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;

import java.sql.Timestamp;

public class postDAOTests {

        JdbcTemplate mockJdbc;
        PostDAO mockPost;
        Post post;
        int postId = 33;
        String q1 = "SELECT * FROM \"posts\" WHERE id=? LIMIT 1;";

        @BeforeEach
        void setupSetPost() {
                mockJdbc = mock(JdbcTemplate.class);
                mockPost = new PostDAO(mockJdbc);
                post = new Post("author", new Timestamp(3101999), "forum", "message", 0, 0, false);
                Mockito.when(mockJdbc.queryForObject(Mockito.eq(q1), Mockito.any(PostDAO.PostMapper.class),
                                Mockito.eq(postId))).thenReturn(post);
        }

        @Test
        void SetPostTest1() {
                PostDAO.setPost(postId, post);
                Mockito.verify(mockJdbc).queryForObject(Mockito.eq(q1), Mockito.any(PostDAO.PostMapper.class),
                                Mockito.eq(postId));
        }

        @Test
        void SetPostTest2() {
                Post newPost = new Post("author2", new Timestamp(4101999), "forum2", "message2", 0, 0, false);
                PostDAO.setPost(postId, newPost);
                String q = "UPDATE \"posts\" SET  author=?  ,  message=?  ,  created=(?)::TIMESTAMPTZ  , isEdited=true WHERE id=?;";
                Mockito.verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyString(),
                                Mockito.any(Timestamp.class), Mockito.anyInt());
        }

        @Test
        void SetPostTest3() {
                Post newPost = new Post("author2", new Timestamp(3101999), "forum", "message", 0, 0, false);
                PostDAO.setPost(postId, newPost);
                String q = "UPDATE \"posts\" SET  author=?  , isEdited=true WHERE id=?;";
                Mockito.verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyInt());
        }

        @Test
        void SetPostTest4() {
                Post newPost = new Post("author", new Timestamp(4101999), "forum", "message", 0, 0, false);
                PostDAO.setPost(postId, newPost);
                String q = "UPDATE \"posts\" SET  created=(?)::TIMESTAMPTZ  , isEdited=true WHERE id=?;";
                Mockito.verify(mockJdbc).update(Mockito.eq(q), Mockito.any(Timestamp.class), Mockito.anyInt());
        }

        @Test
        void SetPostTest5() {
                Post newPost = new Post("author", new Timestamp(3101999), "forum", "message2", 0, 0, false);
                PostDAO.setPost(postId, newPost);
                String q = "UPDATE \"posts\" SET  message=?  , isEdited=true WHERE id=?;";
                Mockito.verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.anyInt());
        }

        @Test
        void SetPostTest6() {
                Post newPost = new Post("author2", new Timestamp(4101999), "forum", "message", 0, 0, false);
                PostDAO.setPost(postId, newPost);
                String q = "UPDATE \"posts\" SET  author=?  ,  created=(?)::TIMESTAMPTZ  , isEdited=true WHERE id=?;";
                Mockito.verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.any(Timestamp.class),
                                Mockito.anyInt());
        }

        @Test
        void SetPostTest7() {
                Post newPost = new Post("author", new Timestamp(4101999), "forum", "message2", 0, 0, false);
                PostDAO.setPost(postId, newPost);
                String q = "UPDATE \"posts\" SET  message=?  ,  created=(?)::TIMESTAMPTZ  , isEdited=true WHERE id=?;";
                Mockito.verify(mockJdbc).update(Mockito.eq(q), Mockito.anyString(), Mockito.any(Timestamp.class),
                                Mockito.anyInt());
        }

}
