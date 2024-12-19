package com.hw.db.DAO;

import com.hw.db.models.*;
import com.hw.db.models.Thread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.hw.db.DAO.PostDAO.getPost;


@Service
@Transactional
public class ThreadDAO {
    private static JdbcTemplate jdbc;
    private static IntegerMapper INT_MAPPER=new IntegerMapper();
    private static ThreadDAO.ThreadMapper THREAD_MAPPER = new ThreadDAO.ThreadMapper();
    private static PostDAO.PostMapper POST_MAPPER = new PostDAO.PostMapper();

    @Autowired
    public ThreadDAO(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public static Thread getThreadById(Integer id){
        String SQL="SELECT * FROM \"threads\" WHERE id=? LIMIT 1;";
        return jdbc.queryForObject(SQL,THREAD_MAPPER,id);
    }
    public static Thread getThreadBySlug(String slug){
//        Number i=Integer.parseInt(slug);
        String SQL="SELECT * FROM \"threads\" WHERE slug::CITEXT = (?)::CITEXT LIMIT 1;";
//        String SQL=" SELECT id FROM \"threads\" WHERE id=1 LIMIT 1;";
        Thread res=jdbc.queryForObject(SQL,THREAD_MAPPER,slug);
        return res;
    }


    public static Integer change(Vote vote, Integer res){
        Integer def=jdbc.queryForObject("SELECT voice FROM \"votes\" WHERE tid=? AND nickname=?;"
                ,Integer.class,vote.getTid(),vote.getNickname());
        String SQL="UPDATE \"votes\" SET voice=? WHERE tid=? AND nickname =?;";
        jdbc.update(SQL,vote.getVoice(),vote.getTid(),vote.getNickname());
        SQL="UPDATE \"threads\" SET votes=votes-?+? WHERE id=?; ";
        jdbc.update(SQL,def,vote.getVoice(),vote.getTid());
        return (res-def+vote.getVoice());
    }

    public static void createVote(Thread th, Vote vote) {
        String SQL="INSERT INTO \"votes\" (nickname, voice,tid) VALUES (?,?,?)";
        jdbc.update(SQL,vote.getNickname(),vote.getVoice(),vote.getTid());
        SQL="UPDATE \"threads\" SET votes=votes+? WHERE id=?;";
        jdbc.update(SQL,vote.getVoice(),th.getId());
    }

    public static void change(Thread before,Thread th){
        List<Object> lst=new ArrayList<>();
        Boolean flag=false;
        String SQL="UPDATE \"threads\" SET";

        if(th.getCreated()!=null)
        {
            SQL+=" created=?::TIMESTAMPTZ ";
            flag=true;
            lst.add(th.getCreated());
        }

        if(th.getSlug()!=null)
        {
            if(flag)
            {
                SQL+=",";
            }
            SQL+=" slug=?::CITEXT ";
            lst.add(th.getSlug());
            flag=true;
        }

        if(th.getAuthor()!=null)
        {
            if(flag)
            {
                SQL+=",";
            }
            SQL+="author=?::CITEXT";
            lst.add(th.getAuthor());
            flag=true;
        }
        if(th.getMessage()!=null)
        {
            if(flag)
            {
                SQL+=",";
            }
            SQL+=" message=? ";
            lst.add(th.getMessage());
            flag=true;
        }
        if(th.getTitle()!=null)
        {
            if(flag)
            {
                SQL+=",";
            }
            SQL+=" title=? ";
            lst.add(th.getTitle());
            flag=true;
        }
        if(flag)
        {
//            SQL+=", isEdited=TRUE ";
            SQL+=" WHERE id=?; ";
            lst.add(before.getId());

            jdbc.update(SQL,lst.toArray());
        }

    }

    public static List<Post> flatSort(Integer id, Integer limit, Integer since, Boolean desc) {
        List<Object> lst = new LinkedList<>();
        String SQL = "SELECT * FROM \"posts\" WHERE thread = ? ";
        lst.add(id);
        if(since != null){
            if(desc != null && desc){
                SQL += " AND id < ?";
            } else {
                SQL += " AND id > ?";
            }
            lst.add(since);
        }
        SQL += " ORDER BY ";
        if( desc != null && desc){
            SQL += " created DESC, id DESC ";
        } else {
            SQL += " created, id";
        }

        if(limit!=null) {
            SQL += " LIMIT ? ";
            lst.add(limit);
        }

        SQL += ";";
        System.out.println(1);
        return jdbc.query(SQL, POST_MAPPER, lst.toArray());
    }


    public static List<Post> treeSort(Integer id, Integer limit, Integer since, Boolean desc) {
        List<Object> lst = new LinkedList<>();
        String SQL;
        SQL = "SELECT * FROM \"posts\" WHERE thread = ? ";
        lst.add(id);
        if (since != null) {
            if (desc != null && desc.equals(true)) {
                SQL += " AND branch < (SELECT branch " +
                        " FROM posts WHERE id = ?) ";
            }
            else{
                SQL += " AND branch > (SELECT branch " +
                        " FROM posts WHERE id = ?) ";
            }
            lst.add(since);

        }
        SQL += " ORDER BY branch";
        if (desc != null && desc) {
            SQL+=" DESC ";
        }

        if(limit!=null) {
            SQL+=" LIMIT ? ";
            lst.add(limit);
        }
        System.out.println(2);
        SQL += ";";
        return jdbc.query(SQL, POST_MAPPER, lst.toArray());
    }



    public static List<Post> parentSort(Integer id, Integer limit, Integer since, Boolean desc) {
        List<Object> lst = new LinkedList<>();
        String SQL;
        SQL = "SELECT * FROM posts ";
        if (limit != null){
            SQL += "JOIN (SELECT id " +
                    " FROM posts WHERE parent IS NULL AND thread = ? ";
            lst.add(id);
            if(since != null) {
                if (desc != null && desc) {
                    SQL += " AND id < (SELECT branch[1]" +
                            " FROM posts WHERE id = ?) ";
                } else {
                    SQL += " AND id > (SELECT branch[1] " +
                            " FROM posts WHERE id = ?) ";
                }
                lst.add(since);
            }
            SQL += " ORDER BY id ";
            if (desc != null && desc){
                SQL += " DESC ";
            }
            SQL += " LIMIT ?) AS B " +
                    "ON posts.branch[1] = B.id ";
            lst.add(limit);
        }
        SQL += "WHERE posts.thread = ?";
        lst.add(id);
        SQL += " ORDER BY posts.branch[1] ";
        if (desc != null && desc){
            SQL += " DESC ";
        }
        SQL += ",posts.branch ,posts.id;";
        System.out.println(3);
        return jdbc.query(SQL, POST_MAPPER, lst.toArray());
    }


        public static List<Post> getPosts(Integer id, Integer limit, Integer since, String sort, Boolean desc) {

        String SQL = "";
        List<Object> lst = new LinkedList<>();
        if(sort == null){
           sort = "flat";
        }
        switch (sort){
            case "flat":
                return flatSort(id, limit, since, desc);
//                SQL = "SELECT * FROM \"posts\" WHERE thread = ? ";
//                lst.add(id);
//                if(since != null){
//                    if(desc != null && desc){
//                        SQL += " AND id < ?";
//                    } else {
//                        SQL += " AND id > ?";
//                    }
//                    lst.add(since);
//                }
//                SQL += " ORDER BY ";
//                if( desc != null && desc){
//                    SQL += " created DESC, id DESC ";
//                } else {
//                    SQL += " created, id";
//                }
//
//                if(limit!=null) {
//                    SQL += " LIMIT ? ";
//                    lst.add(limit);
//                }
//
//                SQL += ";";
//                System.out.println(1);
//                break;
            case "tree":
                return treeSort(id, limit, since, desc);
//                SQL = "SELECT * FROM \"posts\" WHERE thread = ? ";
//                lst.add(id);
//                if (since != null) {
//                    if (desc != null && desc.equals(true)) {
//                        SQL += " AND branch < (SELECT branch " +
//                                " FROM posts WHERE id = ?) ";
//                    }
//                    else{
//                        SQL += " AND branch > (SELECT branch " +
//                                " FROM posts WHERE id = ?) ";
//                    }
//                    lst.add(since);
//
//                }
//                SQL += " ORDER BY branch";
//                if (desc != null && desc) {
//                    SQL+=" DESC ";
//                }
//
//                if(limit!=null) {
//                    SQL+=" LIMIT ? ";
//                    lst.add(limit);
//                }
//                System.out.println(2);
//                SQL += ";";
//                break;
            case "parent_tree":
                return parentSort(id, limit, since, desc);
//                SQL = "SELECT * FROM posts ";
//                if (limit != null){
//                    SQL += "JOIN (SELECT DISTINCT branch[1] as br " +
//                            " FROM posts WHERE thread = ? ";
//                    lst.add(id);
//                    if(since != null) {
//                        if (desc != null && desc) {
//                            SQL += " AND branch[1] < (SELECT branch[1]" +
//                                    " FROM posts WHERE id = ?) ";
//                        } else {
//                            SQL += " AND branch[1] > (SELECT branch[1] " +
//                                    " FROM posts WHERE id = ?) ";
//                        }
//                        lst.add(since);
//                    }
//                    SQL += " ORDER BY br ";
//                    if (desc != null && desc){
//                        SQL += " DESC ";
//                    }
//                    SQL += " LIMIT ?) AS B " +
//                            "ON posts.branch[1] = B.br ";
//                    lst.add(limit);
//                } else {
//                    SQL += " posts.thread = ?;";
//                }
//                SQL += " ORDER BY posts.branch[1] ";
//                if (desc != null && desc){
//                    SQL += " DESC ";
//                }
//                SQL += ",posts.branch ,posts.id;";
//                System.out.println(3);
//                break;
            }
            return null;
           }


    public static final class IntegerMapper implements RowMapper<Integer> {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("id");
        }
    }

    public static void createPosts(Thread th,List<Post> posts, List<User> users) {
        Timestamp curr=new Timestamp(Instant.now().toEpochMilli());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String SQL = " INSERT INTO \"posts\" (message, created, author,forum, thread,parent) VALUES (?,(?)::TIMESTAMP WITH TIME ZONE ,(?)::CITEXT,?,?,?) RETURNING id; ";
        int size = posts.size();

        String SQL2 =" INSERT INTO \"forum_users\" (forum, nickname,email,fullname,about) " +
                "VALUES ((?)::CITEXT,(?)::CITEXT,(?)::citext,?,?); ";
        int i = 0;
        for (Post post: posts
                ) {
                if(post.getParent()!=null){
                   if(!getPost(post.getParent()).getThread().equals(post.getThread()))
                   {
                       throw new DuplicateKeyException("Parent is in another castle.");
                   }
                }
                if(post.getCreated()==null)
                {
                    post.setCreated(curr);
                }

                jdbc.update( connection -> {
                            PreparedStatement pst =
                                    connection.prepareStatement(SQL, PreparedStatement.RETURN_GENERATED_KEYS);
                            pst.setString(1,post.getMessage());
                            pst.setString(2,post.getCreated().toString());
                            pst.setString(3,post.getAuthor());
                            pst.setString(4,post.getForum());
                            pst.setLong(5,post.getThread());
                            pst.setObject(6,post.getParent());
                            return pst;
                        },
                        keyHolder
                );
                post.setId(keyHolder.getKey().intValue());
            try {
                final  int k = i;
                jdbc.update( connection -> {
                            PreparedStatement pst =
                                    connection.prepareStatement(SQL2, PreparedStatement.RETURN_GENERATED_KEYS);

                            pst.setString(1,post.getForum());
                            pst.setString(2,users.get(k).getNickname());

                            pst.setString(3,users.get(k).getEmail());

                            pst.setString(4,users.get(k).getFullname());

                            pst.setString(5,users.get(k).getAbout());
                            return pst;
                        },
                        keyHolder
                );
            } catch (DuplicateKeyException Except){
                System.out.println("Already exists;");
            }
                SetTree(post);
            i++;
        }
        jdbc.update("UPDATE \"forums\" SET posts = posts + ?  WHERE slug=(?)::citext;",size, th.getForum());


    }

    private static void SetTree(Post post) {
        jdbc.update(connection -> {
            PreparedStatement pst=connection.prepareStatement("UPDATE \"posts\" SET branch=? WHERE id=?;", PreparedStatement.RETURN_GENERATED_KEYS);
            if(post.getParent()==null) {
                pst.setArray(1, connection.createArrayOf("INT", new Object[]{post.getId()}));
            } else {
                Post par= getPost(post.getParent());
                ArrayList arr = new ArrayList<Object>(Arrays.asList(par.getBranch()));
                arr.add(post.getId());
                pst.setArray(1, connection.createArrayOf("INT", arr.toArray()));
            }
            pst.setLong(2, post.getId());
            return pst;
        });

    }

    public static final class ThreadMapper implements RowMapper<Thread> {
        public Thread mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Thread th = new Thread();
            th.setId(rs.getInt("id"));
            th.setTitle(rs.getString("title"));
            th.setSlug(rs.getString("slug"));
            th.setCreated(rs.getTimestamp("created"));
            th.setMessage(rs.getString("message"));
            th.setAuthor(rs.getString("author"));
            th.setForum(rs.getString("forum"));
            th.setVotes(rs.getInt("votes"));
            return th;
        }
    }

}
