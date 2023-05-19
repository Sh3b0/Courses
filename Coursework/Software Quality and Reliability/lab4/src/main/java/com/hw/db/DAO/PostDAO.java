package com.hw.db.DAO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hw.db.models.Post;
import com.hw.db.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostDAO {

    private static JdbcTemplate jdbc;
    private static PostDAO.PostMapper POST_MAPPER = new PostDAO.PostMapper();

    @Autowired
    public PostDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static Post getPost(int id)
    {
        return jdbc.queryForObject("SELECT * FROM \"posts\" WHERE id=? LIMIT 1;",POST_MAPPER,id);
    }

    public static void setPost(Integer id, Post post) {
        String SQL="UPDATE \"posts\" SET ";
        Boolean flag=false;
        Post toch=getPost(id);
        List<Object> lst= new ArrayList<Object>();
        if(post.getAuthor()!=null && !toch.getAuthor().equals(post.getAuthor()) )
        {
            SQL += " author=? ";
            flag=true;
            lst.add(post.getAuthor());
        }
        if(post.getMessage()!=null&& !toch.getMessage().equals(post.getMessage()) )
        {
            if(flag)
            {
                SQL += " , ";
            }
            else {
                flag = true;
            }
            SQL+=" message=? ";
            lst.add(post.getMessage());
        }
        if(post.getCreated()!=null&& !toch.getCreated().equals(post.getCreated()) )
        {
            if(flag)
            {
                SQL += " , ";
            }
            else {
                flag = true;
            }
            SQL+=" created=(?)::TIMESTAMPTZ ";
            lst.add(post.getCreated());
        }
        if(flag) {
            SQL+=" , isEdited=true WHERE id=?;";
            lst.add(id);
            jdbc.update(SQL,lst.toArray());
        }
    }


    public static final class PostMapper implements RowMapper<Post> {
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Post th = new Post();
            th.setId(rs.getInt("id"));
            th.setParent(rs.getInt("parent"));
            th.setCreated(rs.getTimestamp("created"));
            th.setMessage(rs.getString("message"));
            th.setAuthor(rs.getString("author"));
            th.setForum(rs.getString("forum"));
            th.setisEdited(rs.getBoolean("isedited"));
            th.setThread(rs.getInt("thread"));
            Array branch=rs.getArray("branch");
            th.setBranch(((Object[]) branch.getArray()));
            return th;
        }
    }
}
