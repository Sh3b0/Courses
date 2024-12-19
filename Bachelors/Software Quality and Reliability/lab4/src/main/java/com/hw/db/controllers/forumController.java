package com.hw.db.controllers;


import com.hw.db.DAO.ForumDAO;
import com.hw.db.DAO.ThreadDAO;
import com.hw.db.DAO.UserDAO;
import com.hw.db.models.Forum;
import com.hw.db.models.User;
import com.hw.db.models.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.db.models.Thread;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import javax.websocket.server.PathParam;
import java.sql.Date;
import java.util.List;
import java.util.TimeZone;

import static com.hw.db.DAO.ForumDAO.Info;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RequestMapping("api/forum")
public class forumController {
    static ObjectMapper mapper = new ObjectMapper();


    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity create(@RequestBody Forum forum) {
        User creator=new User();
        try {
            creator = UserDAO.Search(forum.getUser());
        } catch(DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Владелец форума не найден."));
        }


        forum.setUser(creator.getNickname());
        try {
            ForumDAO.CreateForum(forum);
        } catch (DuplicateKeyException Except) {
            Forum exForum = Info(forum.getSlug());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exForum);
        } catch (DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Message("Что-то на сервере."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(forum);

    }

    @PostMapping(path = "/{slug}/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity create(@PathVariable("slug") String slug,@RequestBody com.hw.db.models.Thread th) {
        User creator=new User();
        Forum forum=new Forum();
        try {
            creator = UserDAO.Search(th.getAuthor());
            forum= Info(slug);
        } catch(DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Владелец форума не найден."));
        }

        th.setAuthor(creator.getNickname());
        th.setForum(forum.getSlug());
        try {
            th=ForumDAO.CreateThread(th,creator);
        } catch (DuplicateKeyException Except) {
            th=ThreadDAO.getThreadBySlug(th.getSlug());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(th);
        }
        catch (DataIntegrityViolationException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Владелец форума не найден."));
        }
        catch (DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Message("Что-то на сервере."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(th);

    }

    @GetMapping(path = "/{slug}/details")
    public ResponseEntity create(@PathVariable("slug") String slug) {

        Forum resp=new Forum();
        try {
            resp= Info(slug);
        }
        catch (IncorrectResultSizeDataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Форум отсутсвует в системе."));
        }
        catch (DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Message("Что-то на сервере."));
        }
        return ResponseEntity.status(HttpStatus.OK).body(resp);

    }

    @GetMapping(path = "/{slug}/threads")
    public ResponseEntity ThreadList(@PathVariable("slug") String slug, @PathParam("limit") Number limit, @PathParam("since") String since, @PathParam("desc") Boolean desc) {
        Forum f=new Forum();
        try {
            f= Info(slug);
        }
        catch (IncorrectResultSizeDataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Форум отсутсвует в системе."));
        }
        List<Thread> resp;
        try {
            resp=ForumDAO.ThreadList(slug,limit,since,desc);
        }

        catch (DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Message("Что-то на сервере."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(resp);

    }

    @GetMapping(path = "/{slug}/users")
    public ResponseEntity UserLst(@PathVariable("slug") String slug, @PathParam("limit") Number limit, @PathParam("since") String since, @PathParam("desc") Boolean desc) {
        List<User> resp;
        try {
            Forum fr= Info(slug);
        }
        catch (DataAccessException exc)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Форум отсутсвует в системе."));
        }
        try {
            resp=ForumDAO.UserList(slug,limit,since,desc);
        }
        catch (IncorrectResultSizeDataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Форум отсутсвует в системе."));
        }
        catch (DataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new Message("Что-то на сервере."));
        }
//        if(resp.isEmpty() && since==null)
//        {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Форум отсутсвует в системе."));
//        }
        return ResponseEntity.status(HttpStatus.OK).body(resp);

    }
}
