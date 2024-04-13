package vttp.nus.miniproject2.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import vttp.nus.miniproject2.models.Game;
import vttp.nus.miniproject2.repositories.GameRepository;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepo;

    public Game createGame(String edition) throws JsonProcessingException {
        Game newGame = new Game();
        newGame.setEdition(edition);
        String uniqueCode = generateUniqueCode();
        newGame.setGameId(uniqueCode);
        gameRepo.saveGame(newGame);
        System.out.println("(GamesService) Created game with gameId: " + uniqueCode);
        return newGame;
    }

    public Game getGameById(String gameId) throws JsonProcessingException {
        return gameRepo.getGame(gameId);
    }

    private String generateUniqueCode() {
        UUID uuid = UUID.randomUUID();
        String code = uuid.toString().substring(0, 6);
        return code;
    }

    public boolean doesGameExist(String gameId) {
        return gameRepo.doesGameExist(gameId);
    }
}