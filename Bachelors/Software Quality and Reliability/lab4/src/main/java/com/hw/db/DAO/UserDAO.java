package com.hw.db.DAO;

import com.hw.db.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.*;

@Service
@Transactional
public class UserDAO {
    private static JdbcTemplate jdbc;
    private static final UserMapper USER_MAPPER = new UserMapper();

    @Autowired
    public UserDAO(JdbcTemplate jdb) {
        jdbc = jdb;
    }



    public static List<User> Create(final User user) {
        String SQL = "INSERT INTO \"users\" (nickname, email,about,fullname) VALUES (?,?,?,?);";
        try {
            jdbc.update(SQL, user.getNickname(), user.getEmail(),user.getAbout(),user.getFullname());
            return null;
        }
        catch (DataAccessException except)
        {
            SQL="SELECT * FROM \"users\" WHERE LOWER(nickname)=LOWER(?) OR LOWER(email)=LOWER(?) LIMIT 2;";
            return jdbc.query(SQL , USER_MAPPER, user.getNickname(), user.getEmail());

        }

    }

    public static User Search(final String nickname){
        String SQL = "SELECT * FROM \"users\" WHERE LOWER(nickname)=LOWER(?) LIMIT 1;";
        return jdbc.queryForObject( SQL,USER_MAPPER,  nickname);
    }

    public static User Info(final String nickname){
        String SQL = "SELECT * FROM \"users\" WHERE LOWER(nickname)=LOWER(?) LIMIT 1;";
        return jdbc.queryForObject( SQL, USER_MAPPER, nickname);
    }

    public  static void Change(final User user) {
        List<Object> dataToChange = new ArrayList<>();
        Boolean flag=false;
        String SQL = "UPDATE \"users\" SET ";
        if(user.getEmail()!=null)
        {
            SQL+=" email=? ";
            dataToChange.add(user.getEmail());
            flag=true;
        }
        if(user.getFullname()!=null)
        {
            if(flag)
            {
                SQL+=",";
            }
            else
            {
                flag=true;
            }
            SQL+=" fullname=? ";
            dataToChange.add(user.getFullname());
        }
        if(user.getAbout()!=null)
        {
            if(flag)
            {
                SQL+=",";
            }
            SQL+=" about=? ";
            dataToChange.add(user.getAbout());
        }
        SQL+= " WHERE nickname=?::CITEXT;" ;
        if(!dataToChange.isEmpty()) {
            dataToChange.add(user.getNickname());
            jdbc.update(SQL, dataToChange.toArray());
        }
    }


    public static final class UserMapper implements RowMapper<User> {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            final User user = new User();
            user.setNickname(rs.getString("nickname"));
            user.setFullname(rs.getString("fullname"));
            user.setEmail(rs.getString("email"));
            user.setAbout(rs.getString("about"));
            return user;
        }
    }
}
