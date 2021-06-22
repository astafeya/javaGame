package org.example.controller;

import org.example.model.Direction;
import org.example.model.Field;
import org.example.model.Tile;
import org.example.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameController {
    @Autowired
    private GameService service;
    
    @GetMapping("/create")
    public ResponseEntity<Integer> start() {
        return ResponseEntity.ok(service.start());
    }

    @GetMapping("/update")
    public ResponseEntity<Tile[]> update(@RequestParam Integer playerID) {
        Tile[] tiles = service.getSubFieldByPlayer(playerID);
        return ResponseEntity.ok(tiles);
    }

    @PutMapping("/change")
    public void changeDirection(@RequestParam Integer playerID, @RequestParam Direction direction) {
        service.changeDirection(playerID, direction);
    }

}
