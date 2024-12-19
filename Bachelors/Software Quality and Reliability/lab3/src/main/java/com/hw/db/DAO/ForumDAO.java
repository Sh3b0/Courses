package com.hw.db.DAO;


import com.hw.db.models.Forum;
import com.hw.db.models.Thread;
import com.hw.db.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hw.db.DAO.ThreadDAO;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

@Service
@Transactional
public class ForumDAO {
//    private static final ForumMapper FORUM_MAPPER = new ForumMapper();
    private static JdbcTemplate jdbc;
    private static ForumMapper FORUM_MAPPER=new ForumMapper();
    private static ThreadDAO.ThreadMapper THREAD_MAPPER = new ThreadDAO.ThreadMapper();
    private static UserDAO.UserMapper USER_MAPPER=new UserDAO.UserMapper();

    @Autowired
    public ForumDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static void CreateForum(Forum forum) {
        String SQL = "INSERT INTO \"forums\" (slug, title, creator) VALUES (?, ?, (?)::CITEXT);";
        jdbc.update(SQL, forum.getSlug(), forum.getTitle(), forum.getUser());
    }

    public static Thread CreateThread(Thread thread, User creator){
        if(thread.getCreated()==null) {
            thread.setCreated(new Timestamp(System.currentTimeMillis()));
        }
        Thread res;
        String SQL;
        String SQL2 =" INSERT INTO \"forum_users\" (forum, nickname,email,fullname,about) " +
                "VALUES ((?)::CITEXT,(?)::CITEXT,(?)::citext,?,?); ";
        SQL= "INSERT INTO \"threads\" (author,forum,message,title,slug,created) VALUES ((?)::CITEXT,(?)::CITEXT,?,?,?,(?)::TIMESTAMPTZ ) RETURNING *; ";
        res=jdbc.queryForObject(SQL, THREAD_MAPPER, thread.getAuthor(), thread.getForum(), thread.getMessage(), thread.getTitle(), thread.getSlug(), thread.getCreated());
        try {
            jdbc.update(SQL2 , thread.getForum(), thread.getAuthor(), creator.getEmail(),
                    creator.getFullname(),creator.getAbout());
        } catch (DuplicateKeyException Except){
            System.out.println("Already exists;");
        }
        SQL="UPDATE \"forums\" SET threads=threads+1  WHERE slug = (?);";
        jdbc.update(SQL,thread.getForum());
        return res;

     }

    public static Forum Info(String slug){
        String SQL = "SELECT * FROM \"forums\" WHERE slug=(?)::citext LIMIT 1;" ;
        return jdbc.queryForObject(SQL,FORUM_MAPPER,slug);
    }


    public static List<Thread> ThreadList(String slug, Number limit, String since, Boolean desc) {
        String SQL="SELECT * FROM threads WHERE forum = (?)::CITEXT";
        List<Object> conditions=new ArrayList<>();
        conditions.add(slug);
        if(since!=null)
        {if(desc!=null && desc) {
            SQL += " AND created<=(?)::TIMESTAMP WITH TIME ZONE";
        }
        else
        {
            SQL += " AND created>=(?)::TIMESTAMP WITH TIME ZONE";

        }
            conditions.add(since);
        }
        SQL+=" ORDER BY created";
        if(desc!=null && desc)
        {
            SQL+=" desc";
        }
        if(limit!=null)
        {
            SQL+=" LIMIT ?";
            conditions.add(limit);
        }
        SQL+=";";
        return jdbc.query(SQL ,conditions.toArray() ,THREAD_MAPPER );
    }

    public static List<User> UserList(String slug, Number limit, String since, Boolean desc) {
        String SQL="SELECT nickname,fullname,email,about FROM forum_users" +
                " WHERE forum = (?)::citext";
        List<Object> conditions=new ArrayList<>();
        conditions.add(slug);
        if(since!=null)
        {
            if(desc!=null && desc)
            {
                SQL+=" AND  nickname < (?)::citext";
                conditions.add(since);
            }
            else {
                SQL +=" AND  nickname > (?)::citext";
                conditions.add(since);
            }
        }
        SQL+=" ORDER BY nickname";
        if(desc!=null && desc)
        {
            SQL+=" desc";
        }
        if(limit!=null)
        {
            SQL+=" LIMIT ?";
            conditions.add(limit);
        }
        SQL+=";";
        return jdbc.query(SQL  , conditions.toArray(), USER_MAPPER );
    }

    public static final class ForumMapper implements RowMapper<Forum> {
        public Forum mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Forum forum = new Forum();
            forum.setTitle(rs.getString("title"));
            forum.setSlug(rs.getString("slug"));
            forum.setUser(rs.getString("creator"));
            forum.setPosts(rs.getInt("posts"));
            forum.setThreads(rs.getInt("threads"));
            return forum;
        }
    }



}
