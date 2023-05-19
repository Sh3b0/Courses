package com.hw.db.controllers;

import com.hw.db.DAO.UserDAO;
import com.hw.db.models.User;
import com.hw.db.models.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class userController {


    @PostMapping(path = "/{user}/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity create(@PathVariable(name="user") String nick,@RequestBody User user) throws JsonProcessingException {

        user.setNickname(nick);
        List<User> resp = UserDAO.Create(user);
        if(resp==null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        }
         else
         {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp.toArray());
         }
    }
    @PostMapping(path = "/{nick}/profile", consumes = "application/json", produces = "application/json")
    public ResponseEntity change(@PathVariable(name="nick") String nick,@RequestBody User user){

        user.setNickname(nick);
        User res=new User();
        try{
            res=UserDAO.Search(nick);
        }
        catch (IncorrectResultSizeDataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cannot find user"));
        }
        try {
            UserDAO.Change(user);

        }
        catch (DuplicateKeyException Except)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("Cannot find user"));
        }
        if(!(user.getEmail()==null))
        {
            res.setEmail(user.getEmail());
        }
        if(!(user.getAbout()==null))
        {
            res.setAbout(user.getAbout());
        }
        if(!(user.getFullname()==null))
        {
            res.setFullname(user.getFullname());
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);


//        user.setNickname(nick);
//
//        try {
//            UserDAO.Change(user);
//        } catch (DuplicateKeyException Except)
//        {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("Cannot find user"));
//        }
//        catch (DataAccessException Except)
//        {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cannot find user"));
//        }
//        try{
//            User res=new User();
//            res=UserDAO.Search(nick);
//            if(!(      ((res.getFullname()==user.getFullname()) || (user.getFullname()==null))
//                    && ((user.getEmail() == res.getEmail()) || (user.getEmail()==null))
//                    && ((user.getAbout() == res.getAbout()) || (user.getAbout()==null)) ))
//            {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("Cannot find user"));
//            }
//        }
//        catch (DataAccessException Except)
//        {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cannot find user"));
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    @GetMapping(path = "/{nick}/profile")
    public ResponseEntity info(@PathVariable(name="nick") String nick) throws JsonProcessingException {
        User response;
        try{
            response=UserDAO.Info(nick);
        }
        catch (IncorrectResultSizeDataAccessException Except)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message("Cannot find user"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


}