package company.gdx.server.ws;

import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.IOException;

public interface ConnectListener {
    void handle(StandardWebSocketSession session) throws IOException;
}
