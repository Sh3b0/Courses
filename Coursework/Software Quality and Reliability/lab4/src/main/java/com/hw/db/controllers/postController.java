package com.hw.db.controllers;

import com.hw.db.DAO.ForumDAO;
import com.hw.db.DAO.PostDAO;
import com.hw.db.DAO.ThreadDAO;
import com.hw.db.DAO.UserDAO;
import com.hw.db.models.Message;
import com.hw.db.models.Post;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RequestMapping("/api/post")
public class postController {

    @PostMapping("{id}/details")
    public ResponseEntity create(@PathVariable("id") Integer id, @RequestBody Post post) {
        Post resp=new Post();
        try {

            PostDAO.setPost(id,post);
            resp=PostDAO.getPost(id);
        } catch(DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Владелец форума не найден."));
        }
        Map <String,Object> ret=new HashMap<>();
//        ret.put("author",resp.getAuthor());
//        ret.put("created",resp.getCreated().toLocalDateTime());
//        ret.put("forum",resp.getForum());
//        ret.put("id",resp.getId());
//        ret.put("isEdited",resp.getisEdited());
//        ret.put("message",resp.getMessage());
//        ret.put("thread",resp.getThread());
//        resp.setisEdited(false);
        return ResponseEntity.status(HttpStatus.OK).body(resp);

    }

    @GetMapping("{id}/details")
    public ResponseEntity create(@PathVariable("id") Integer id,  @RequestParam(value = "related", required = false) String[] rel) {
        Map<String,Object> mp=new HashMap<>();
        Post resp=new Post();
        try {
            resp=PostDAO.getPost(id);
            mp.put("post",resp);
            if(rel!=null){
            if( Arrays.asList(rel).contains("user"))
            {
                mp.put("author", UserDAO.Info(resp.getAuthor()));
            }
            if( Arrays.asList(rel).contains("thread"))
            {
                mp.put("thread", ThreadDAO.getThreadById(resp.getThread()));
            }
            if( Arrays.asList(rel).contains("forum"))
            {
                mp.put("forum", ForumDAO.Info(resp.getForum()));
            }
            }
        } catch(DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Владелец форума не найден."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(mp);

    }

}
