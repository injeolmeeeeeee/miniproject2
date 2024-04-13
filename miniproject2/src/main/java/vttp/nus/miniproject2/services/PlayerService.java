package vttp.nus.miniproject2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import vttp.nus.miniproject2.models.Player;
import vttp.nus.miniproject2.repositories.GameRepository;
import vttp.nus.miniproject2.repositories.PlayerRepository;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepo;

    public void savePlayer(String gameId, String playerName) {
        playerRepository.savePlayer(gameId, playerName);
    }

    public Player getPlayer(String gameId, String playerName) {
        return playerRepository.getPlayer(gameId, playerName);
    }

    public boolean isPlayerNameUnique(String gameId, String playerName) {
        Player existingPlayer = getPlayer(gameId, playerName);
        return existingPlayer == null;
    }

    public List<Player> getPlayersInGameLobby(String gameId) throws JsonProcessingException {
        return playerRepository.getAllPlayers(gameId);
    }

    public void addPlayerToGameLobby(String gameId, Player newPlayer) throws JsonProcessingException {
        if (doesGameExist(gameId)) {
            System.out.println("Player name received: " + newPlayer.getName());
            playerRepository.addPlayerToGame(gameId, newPlayer);
        } else {
            throw new RuntimeException("Game not found with ID: " + gameId);
        }
    }

    public boolean doesGameExist(String gameId) {
        return gameRepo.doesGameExist(gameId);
    }

    public boolean checkPlayerNameExistsInGame(String playerName, String gameId) {
        try {
            List<Player> players = playerRepository.getAllPlayers(gameId);
            for (Player player : players) {
                if (player.getName().equals(playerName)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void saveResponsesAndScores(String gameId, Player player) throws JsonMappingException, JsonProcessingException {
        playerRepository.savePlayerResponses(gameId, player);
    }

	public void updatePlayerList(String gameId, List<Player> players) throws JsonProcessingException {
		playerRepository.updatePlayerList(gameId, players);
	}

    public void clearPlayerResponses(String gameId) {
        playerRepository.clearPlayerResponses(gameId);
    }
}