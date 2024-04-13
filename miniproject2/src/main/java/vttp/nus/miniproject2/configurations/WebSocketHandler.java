package vttp.nus.miniproject2.configurations;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vttp.nus.miniproject2.models.Player;
import vttp.nus.miniproject2.models.Question;
// import vttp.nus.miniproject2.repositories.QuestionRepository;
import vttp.nus.miniproject2.services.PlayerService;
import vttp.nus.miniproject2.services.QuestionService;
// import vttp.nus.miniproject2.services.QuotesService;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);

    private final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final Map<String, List<Player>> gameSessions = new ConcurrentHashMap<>();

    @Autowired
    private PlayerService playerService;

    // @Autowired
    // private QuotesService quotesService;

    // @Autowired
    // private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("messgae received: " + message);
        try {
            JsonNode messageNode = objectMapper.readTree(message.getPayload());
            String gameId = messageNode.path("gameId").asText();
            String messageContent = messageNode.path("content").asText();
            // String messageType = messageNode.path("type").asText();
            // String playerName = messageNode.path("playerName").asText();

            switch (messageContent) {
                case "start_game":
                    handleStartGameEvent(gameId);
                    break;
                case "new_player":
                    handleNewPlayerEvent(session, gameId);
                    break;
                case "response_submit":
                    handleResponseEvent(session, gameId);
                    break;
                case "new_round":
                    handleNewRoundEntered(gameId);
                    break;
                case "end":
                    handleResponseEvent(session, gameId);
                    break;
                default:
                    logger.warn("Received unrecognized message content: {}", messageContent);
                    break;
            }
        } catch (IOException e) {
            logger.error("Error handling text message: {}", e.getMessage());
        }
    }

    public void handleNewRoundEntered(String gameId) {
        // playerService.clearPlayerResponses(gameId);
        String message = "{\"type\": \"next_question\", \"content\": \"\"}";
        broadcastMessage(message);
    }

    private String extractGameIdFromURI(URI uri) {
        String queryString = uri.getQuery();
        System.out.println(queryString);
        if (queryString != null) {
            String[] queryParams = queryString.split("&");
            for (String param : queryParams) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "gameCode".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private void handleNewPlayerEvent(WebSocketSession session, String gameId) {
        Player player = (Player) session.getAttributes().get("player");
        if (player != null) {
            gameSessions.computeIfAbsent(gameId, key -> new ArrayList<>()).add(player);
            logger.info("Player {} joined the game lobby", player.getName());
            send(session);
        } else {
            logger.warn("No player associated with the session");
        }
    }

    private void handleResponseEvent(WebSocketSession session, String gameId) throws JsonProcessingException {
        Player player = (Player) session.getAttributes().get("player");
        if (player != null) {
            gameSessions.computeIfAbsent(gameId, key -> new ArrayList<>()).add(player);
            send(session);
        } else {
            logger.warn("No player associated with the session");
        }
    }

    private void send(WebSocketSession session) {
        String gameId = extractGameIdFromURI(session.getUri());
        try {
            List<Player> players = playerService.getPlayersInGameLobby(gameId);
            String jsonString = objectMapper.writeValueAsString(players);
            System.out.println("Serialized JSON: " + jsonString); 
            String message = "{\"type\": \"player_list\", \"content\": " + jsonString + "}";
            broadcastMessage(message);
        } catch (JsonProcessingException e) {
            logger.error("Error sending updated player list: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws JsonProcessingException {
        String gameId = extractGameIdFromURI(session.getUri());
        String origin = session.getHandshakeHeaders().getFirst("Origin");
        logger.info("WebSocket connection established from origin: {}", origin);

        List<Player> players = playerService.getPlayersInGameLobby(gameId);
        if (!players.isEmpty()) {
            Player player = players.get(0);
            session.getAttributes().put("player", player);
            addToSessions(gameId, players, session);
            logger.info("Player {} added to WebSocket session", player.getName());
        } else {
            logger.warn("No players found for gameId ", gameId);
        }
        send(session);
    }

    private void addToSessions(String gameId, List<Player> players, WebSocketSession session) {
        sessions.computeIfAbsent(gameId, key -> new ArrayList<>()).add(session);
        gameSessions.put(gameId, players);
    }

    // private void sendMessageToAllSessions(String message) {
    // sessions.values().forEach(sessionList -> sessionList.forEach(session ->
    // sendMessage(session, message)));
    // }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws JsonProcessingException {
        logger.info("WebSocket connection closed. Session ID: {}, Status: {}", session.getId(), status);
        removePlayerFromList(session);
        // sessions.remove(session.getId());
    }

    private void removePlayerFromList(WebSocketSession session) throws JsonProcessingException {
        Player player = (Player) session.getAttributes().get("player");
        if (player != null) {
            String gameId = extractGameIdFromURI(session.getUri());
            List<Player> players = playerService.getPlayersInGameLobby(gameId);
            if (players != null) {
                players.remove(player);
                logger.info("Player {} removed from the player list", player.getName());
                try {
                    playerService.updatePlayerList(gameId, players);
                } catch (Exception e) {
                    logger.error("Error updating player list in the data store: {}", e.getMessage());
                }
                send(session);
            } else {
                logger.warn("Player list not found for gameId: {}", gameId);
            }
        } else {
            logger.warn("No player associated with the closed session");
        }
    }

    private void handleStartGameEvent(String gameId) throws JsonProcessingException {
        List<Question> questions =  questionService.selectNextQuestion(gameId);
        String questionJson = objectMapper.writeValueAsString(questions);
        String message = "{\"type\": \"questions\", \"content\": " + questionJson + "}";
        broadcastMessage(message);
        logger.info("Handling start game event...");
        String startMessage = "{\"type\": \"game_start\", \"content\": \"\"}";
        broadcastMessage(startMessage);
    }

    private void broadcastMessage(String message) {
        sessions.values().forEach(sessionList -> sessionList.forEach(session -> sendMessage(session, message)));
    }

    public void broadcastMessageToSessions(String message, List<WebSocketSession> sessions) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                
            }
        }
    }

    private void sendMessage(WebSocketSession session, String message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
            }
        } else {
            logger.warn("Session {} is closed", session.getId());
        }
    }
}