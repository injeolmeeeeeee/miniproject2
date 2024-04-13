package vttp.nus.miniproject2.configurations;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketSessionManager {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    public Set<WebSocketSession> getAllSessions() {
        return sessions;
    }
}
