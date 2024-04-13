package vttp.nus.miniproject2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import vttp.nus.miniproject2.models.Player;
import vttp.nus.miniproject2.services.PlayerService;

@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    
@PostMapping("/player")
public ResponseEntity<?> savePlayer(@RequestParam String gameId, @RequestParam String playerName) {
    
    if (!playerService.isPlayerNameUnique(gameId, playerName)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player name is not unique.");
    }
    
    playerService.savePlayer(gameId, playerName);
    return ResponseEntity.ok().build();
}

    @GetMapping("/player/{gameId}/{playerName}")
    public ResponseEntity<Player> getPlayer(@PathVariable String gameId, @PathVariable String playerName) {
        return ResponseEntity.ok(playerService.getPlayer(gameId, playerName));
    }

    @PostMapping("/responses-scores")
    public void saveResponsesAndScores(@RequestParam String gameId, @RequestBody Player player) throws JsonMappingException, JsonProcessingException {
        System.out.println("Received gameId: " + gameId);
        System.out.println("Received player: " + player.getName());
        System.out.println("Received response: " + player.getResponses());
        System.out.println("Received scores: " + player.getScore());
        playerService.saveResponsesAndScores(gameId, player);
    }
}
