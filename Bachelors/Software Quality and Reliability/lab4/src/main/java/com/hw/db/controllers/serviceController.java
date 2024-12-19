package com.hw.db.controllers;

import com.hw.db.DAO.ServiceDAO;
import com.hw.db.models.*;
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

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RequestMapping("/api/service")
public class serviceController {

    @PostMapping(path = "/clear" ,consumes = "application/json", produces = "application/json")
    public ResponseEntity Clear() {
        ServiceDAO.Clear();
        return ResponseEntity.status(HttpStatus.OK).body(new Message("Очистка базы успешно завершена"));
    }


    @GetMapping(path = "/status" )
    public ResponseEntity Status() {
        Status resp=ServiceDAO.Status();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}
