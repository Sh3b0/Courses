package com.hw.db.controllers;

import com.hw.db.DAO.ServiceDAO;
import com.hw.db.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RequestMapping("/api/service")
public class ServiceController {

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
