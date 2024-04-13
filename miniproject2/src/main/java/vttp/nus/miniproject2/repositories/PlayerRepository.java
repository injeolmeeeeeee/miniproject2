package vttp.nus.miniproject2.repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vttp.nus.miniproject2.models.Player;

@Repository
public class PlayerRepository {

    private static final Logger logger = LoggerFactory.getLogger(PlayerRepository.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    public void savePlayer(String gameId, String playerName) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(gameId, playerName, playerName);
    }

    public Player getPlayer(String gameId, String playerName) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String playerNameStored = hashOperations.get(gameId, playerName);
        Player player = new Player();
        player.setName(playerNameStored);
        return player;
    }

    @SuppressWarnings("unchecked")
public void savePlayerResponses(String gameId, Player player) {
    logger.info("(PlayerRepo) savePlayerResponses gameId: " + gameId);
    HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

    try {
        String gameDataString = hashOperations.get(gameId, "players");
        ObjectMapper objectMapper = new ObjectMapper();

        List<Map<String, Object>> players;
        if (gameDataString != null && !gameDataString.isEmpty()) {
            players = objectMapper.readValue(gameDataString, new TypeReference<List<Map<String, Object>>>() {});
        } else {
            players = new ArrayList<>();
        }

        boolean playerFound = false;
        for (Map<String, Object> playerData : players) {
            String playerName = (String) playerData.get("name");
            if (playerName.equals(player.getName())) {
                List<String> responseStrings = (List<String>) playerData.get("responses");
                Integer score = (Integer) playerData.get("score");

                if (responseStrings == null) {
                    responseStrings = new ArrayList<>();
                    playerData.put("responses", responseStrings);
                }

                // Add response to the list
                responseStrings.addAll(player.getResponses());

                // Update score
                if (score == null) {
                    score = 0;
                }
                score += player.getScore(); //line 77

                // Update player data
                playerData.put("responses", responseStrings);
                playerData.put("score", score);

                playerFound = true;
                break; // Exit loop once player is found
            }
        }

        if (!playerFound) {
            // If player not found, create a new player entry
            Map<String, Object> newPlayerData = new HashMap<>();
            newPlayerData.put("name", player.getName());
            newPlayerData.put("responses", player.getResponses());
            newPlayerData.put("score", player.getScore());
            players.add(newPlayerData);
        }

        String updatedGameDataString = objectMapper.writeValueAsString(players);
        hashOperations.put(gameId, "players", updatedGameDataString);
        logger.info("Updated game data for game {}: {}", gameId, updatedGameDataString);
    } catch (IOException e) {
        logger.error("Error reading or updating game data", e);
    }
}


    public void addPlayerToGame(String gameId, Player player) throws JsonProcessingException {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String playersJson = hashOperations.get(gameId, "players");

        List<Player> players;
        if (playersJson != null) {
            players = objectMapper.readValue(playersJson, new TypeReference<List<Player>>() {
            });
        } else {
            players = new ArrayList<>();
        }

        players.add(player);
        String updatedPlayersJson = objectMapper.writeValueAsString(players);
        hashOperations.put(gameId, "players", updatedPlayersJson);
    }

    @SuppressWarnings("unchecked")
public List<Player> getAllPlayers(String gameId) {
    HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
    String playersJson = hashOperations.get(gameId, "players");

    List<Player> players = new ArrayList<>();
    if (playersJson != null) {
        try {
            List<Map<String, Object>> playersList = objectMapper.readValue(playersJson,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            for (Map<String, Object> playerMap : playersList) {
                Player player = new Player();
                player.setName((String) playerMap.get("name"));
                
                // Set score
                Integer score = (Integer) playerMap.get("score");
                player.setScore(score != null ? score : 0);

                // Set responses
                List<String> responseStrings = (List<String>) playerMap.get("responses");
                player.setResponses(responseStrings != null ? responseStrings : new ArrayList<>());

                players.add(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return players;
}




    public void updatePlayerList(String gameId, List<Player> players) throws JsonProcessingException {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String updatedPlayersJson = objectMapper.writeValueAsString(players);
        hashOperations.put(gameId, "players", updatedPlayersJson);
        System.out.println("Updated player list for gameId: " + gameId);
    }

    // public void clearPlayerResponses(String gameId) {
    // HashOperations<String, String, Object> hashOperations =
    // redisTemplate.opsForHash();

    // List<String> playerNames = getPlayerNames(gameId);
    // if (playerNames != null) {
    // for (String playerName : playerNames) {
    // Player player = getPlayer(gameId, playerName);
    // if (player != null) {
    // player.setResponses(new ArrayList<>());
    // player.setScore(0);

    // hashOperations.put(gameId, playerName, player);
    // }
    // }
    // }
    // }

    public List<String> getPlayerNames(String gameId) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

        Object playerNamesObject = hashOperations.get(gameId, "players");
        if (playerNamesObject != null && playerNamesObject instanceof String) {
            String playerNamesString = (String) playerNamesObject;
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                List<String> playerNames = objectMapper.readValue(playerNamesString, new TypeReference<List<String>>() {
                });

                return playerNames;
            } catch (JsonProcessingException e) {
                logger.error("(PlayerRepo) getPlayerNames exception: " + e.getMessage());
            }
        }

        return null;
    }
}