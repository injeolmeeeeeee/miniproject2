package vttp.nus.miniproject2.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import vttp.nus.miniproject2.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GameRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    ObjectMapper objectMapper = new ObjectMapper();


    public boolean doesGameExist(String gameId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.hasKey(gameId, "game");
    }

    public Game getGame(String gameId) throws JsonProcessingException {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String jsonGame = hashOperations.get(gameId, "game");
        System.out.println("(GameRepo) Retrieved game with gameId: " + gameId + " from Redis");
        return objectMapper.readValue(jsonGame, Game.class);
    }

    public void saveGame(Game game) throws JsonProcessingException {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String jsonGame = objectMapper.writeValueAsString(game);
        hashOperations.put(game.getGameId(), "game", jsonGame);
        System.out.println("(GameRepo) Saved game with gameId: " + game.getGameId() + " in Redis");
    }

    public String getGameId(String key) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        return valueOps.get(key);
    }

    public List<Game> getAllGames() throws JsonProcessingException {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        List<String> jsonGames = hashOperations.values("games");
        List<Game> games = new ArrayList<>();
        for (String jsonGame : jsonGames) {
            Game game = objectMapper.readValue(jsonGame, Game.class);
            games.add(game);
        }
        return games;
    }
}