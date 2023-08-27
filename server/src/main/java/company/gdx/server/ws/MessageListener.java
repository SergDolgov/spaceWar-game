package company.gdx.server.ws;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

public interface MessageListener {
    void handle(StandardWebSocketSession session, JsonNode message);
}
