package com.hw.db.DAO;


import com.hw.db.models.Forum;
import com.hw.db.models.Status;
import com.hw.db.models.Thread;
import com.hw.db.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class ServiceDAO {
    private static JdbcTemplate jdbc;
    @Autowired
    public ServiceDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static void Clear(){
        String SQL= "TRUNCATE votes,posts,forums,threads,users,votes CASCADE ;";
        jdbc.update(SQL);
    }

    public static Status Status(){
        Status ret=new Status();
        String SQL="SELECT count(id)  FROM \"forums\" ;";
        ret.setForum(jdbc.queryForObject(SQL,Integer.class));
        SQL="SELECT count(id)  FROM \"users\" ;";
        ret.setUser(jdbc.queryForObject(SQL,Integer.class));
        SQL="SELECT count(*) FROM \"threads\" ;";
        ret.setThread(jdbc.queryForObject(SQL,Integer.class));
        SQL="SELECT count(*) FROM \"posts\" ;";
        ret.setPost(jdbc.queryForObject(SQL,Integer.class));
        return ret;
    }



}
