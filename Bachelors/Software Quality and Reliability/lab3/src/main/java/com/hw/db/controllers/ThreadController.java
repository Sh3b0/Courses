package com.hw.db.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.db.DAO.ThreadDAO;
import com.hw.db.DAO.UserDAO;
import com.hw.db.models.*;
import com.hw.db.models.Thread;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RequestMapping("/api/thread")
public class ThreadController {

    public Thread CheckIdOrSlug (String slug_or_id) {
        if (slug_or_id.matches("\\d+")) {
            return ThreadDAO.getThreadById(Integer.parseInt(slug_or_id));
        } else {
            return ThreadDAO.getThreadBySlug(slug_or_id);
        }
    }


    @PostMapping(path = "/{slug_or_id}/create" ,consumes = "application/json", produces = "application/json")
    public ResponseEntity createPost(@PathVariable(name="slug_or_id") String slug, @RequestBody List<Post> posts){
        Thread th=new Thread();
        List<User> users = new ArrayList<>();
        try
        {
            th=CheckIdOrSlug(slug);

//            post.setThread("some");
            int i=0;
            for (Post post:posts
                 ) {

                post.setForum(th.getForum());
                users.add(UserDAO.Info(post.getAuthor()));
                post.setAuthor(users.get(i).getNickname());
                post.setThread(th.getId());
                i++;
            }
        }
        catch (DataAccessException Exc)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Раздел не найден."));
        }
        try {
            ThreadDAO.createPosts(th,posts,users);
        }
        catch (DataAccessException Exc) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("Хотя бы один родительский пост отсутсвует в текущей ветке обсуждения."));
        }



        return ResponseEntity.status(HttpStatus.CREATED).body(posts);

    }
    @GetMapping(path="/{slug_or_id}/posts")
    public ResponseEntity Posts(@PathVariable(name="slug_or_id") String slug, @JsonProperty("limit") Integer limit, @JsonProperty("since") Integer since,@JsonProperty("sort") String sort,@JsonProperty("desc") Boolean desc){
        Thread th;
        try
        {
            th=CheckIdOrSlug(slug);

        }
        catch (DataAccessException Exc)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Раздел не найден."));
        }

        List<Post> res=ThreadDAO.getPosts(th.getId(),limit,since,sort,desc);

        return ResponseEntity.status(HttpStatus.OK).body(res);

    }

    @PostMapping(path="/{slug_or_id}/details")
    public ResponseEntity change(@PathVariable(name="slug_or_id") String slug,@RequestBody Thread thread){
        Thread th=new Thread();
        try
        {
            th=CheckIdOrSlug(slug);
            ThreadDAO.change(th,thread);
        }
        catch (DataAccessException Exc)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Раздел не найден."));
        }
        th=CheckIdOrSlug(th.getId().toString());
        return ResponseEntity.status(HttpStatus.OK).body(th);

    }

    @GetMapping(path="/{slug_or_id}/details")
    public ResponseEntity info(@PathVariable(name="slug_or_id") String slug){
        Thread th=new Thread();
        try
        {
            th=CheckIdOrSlug(slug);

        }
        catch (DataAccessException Exc)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Раздел не найден."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(th);

    }

    @PostMapping(path = "/{slug_or_id}/vote" ,consumes = "application/json", produces = "application/json")
    public ResponseEntity createVote(@PathVariable(name="slug_or_id") String slug, @RequestBody Vote vote){
        Thread th=new Thread();
        Integer votes=0;
        try
        {
            th=CheckIdOrSlug(slug);
            votes=th.getVotes();
            User user = UserDAO.Info(vote.getNickname());
            vote.setNickname(user.getNickname());
            vote.setTid(th.getId());
        }
        catch (DataAccessException Exc)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Раздел не найден."));
        }
        try {
            ThreadDAO.createVote(th,vote);
            votes+=vote.getVoice();
        }
        catch (DuplicateKeyException Exc) {
            try {
                votes=ThreadDAO.change(vote, votes);
            }
            catch (DuplicateKeyException Except){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("Хотя бы один родительский пост отсутсвует в текущей ветке обсуждения."));
            }
        }
        th.setVotes(votes);

        return ResponseEntity.status(HttpStatus.OK).body(th);

    }
}
