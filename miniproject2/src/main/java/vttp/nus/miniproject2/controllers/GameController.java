package vttp.nus.miniproject2.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import vttp.nus.miniproject2.models.Game;
import vttp.nus.miniproject2.models.Player;
import vttp.nus.miniproject2.services.GameService;
import vttp.nus.miniproject2.services.PlayerService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

     @Autowired
    private HttpSession httpSession;

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestParam String edition) {
        try {
            Game newGame = gameService.createGame(edition);
            logger.info("Created new game with ID: {}", newGame.getGameId());
            return new ResponseEntity<>(newGame, HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            logger.error("Error creating game:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGameById(@PathVariable String gameId) {
        try {
            Game game = gameService.getGameById(gameId);
            return ResponseEntity.ok(game);
        } catch (JsonProcessingException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/game-lobby/{gameId}/players")
public ResponseEntity<Player> addPlayerToGameLobby(@PathVariable String gameId, @RequestBody String playerJson) {
    try {
        logger.info("Received request to add player to game lobby. Game ID: {}", gameId);

        ObjectMapper objectMapper = new ObjectMapper();
        Player newPlayer = objectMapper.readValue(playerJson, Player.class);

        logger.info("Received player details: {}", newPlayer);

        if (!gameService.doesGameExist(gameId)) {
            logger.error("Game not found with ID: {}", gameId);
            return ResponseEntity.notFound().build();
        }

        playerService.addPlayerToGameLobby(gameId, newPlayer);
        httpSession.setAttribute("player", newPlayer); // Here you set the player in the session

        logger.info("Player added to game lobby successfully. Player: {}", newPlayer);
        
        // Log player information stored in session
        logger.info("Player stored in session: {}", httpSession.getAttribute("player"));

        return ResponseEntity.ok(newPlayer);
    } catch (Exception e) {
        logger.error("Error occurred while adding player to game lobby. Game ID: {}", gameId, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    @GetMapping("/checkPlayerNameExists")
    public ResponseEntity<Boolean> checkPlayerNameExists(@RequestParam String name, @RequestParam String gameId) {
        try {
            boolean exists = playerService.checkPlayerNameExistsInGame(name, gameId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}