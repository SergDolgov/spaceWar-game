package company.gdx.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import company.gdx.server.actors.Player;
import company.gdx.server.ws.WebSocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

@Component
public class GameLoop extends ApplicationAdapter {
    private static final float frameRate = 1 / 30f;
    private final WebSocketHandler webSocketHandler;
    private float lastRender = 0;
    private final Json json;
    private final ObjectMap<String, Player> players = new ObjectMap<>();
    private final Array<Player> stateToSend= new Array<>();
    private final ForkJoinPool pool = ForkJoinPool.commonPool();

    public GameLoop(WebSocketHandler webSocketHandler, Json json) {
        this.webSocketHandler = webSocketHandler;
        this.json = json;
    }

    @Override
    public void create() {
        webSocketHandler.setConnectListener(session -> {
            Player player = new Player();
            player.setId(session.getId());
            player.setX(780);
            player.setY(64);
            players.put(session.getId(), player);
            try {
                session
                        .getNativeSession()
                        .getBasicRemote()
                        .sendText(
                                String.format("{\"class\":\"sessionKey\",\"id\":\"%s\"}", session.getId())
                        );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        webSocketHandler.setDisconnectListener(session -> {
            sendToAllPlayers(
                    String.format("{\"class\":\"evict\",\"id\":\"%s\"}", session.getId())
            );
            players.remove(session.getId());
        });

        webSocketHandler.setMessageListener((session, message) -> {
            pool.execute(() -> {
                String type = message.get("type").asText();
                switch (type){
                    case "state":
                        Player player = players.get(session.getId());
                        player.setLeftPressed(message.get("leftPressed").asBoolean());
                        player.setRightPressed(message.get("rightPressed").asBoolean());
                        player.setUpPressed(message.get("upPressed").asBoolean());
                        player.setDownPressed(message.get("downPressed").asBoolean());
                        player.setAngle((float) message.get("angle").asDouble());
                        player.setSpeed(message.get("speed").asInt());
                        player.setHp(message.get("hp").asInt());
                        player.setHpMax(message.get("hpMax").asInt());
                        break;
                    default:
                        throw new RuntimeException("Unknown WS object type " + type);
                }
                //System.out.println("Received " + message.toString());
            });
        });
    }

    @Override
    public void render() {
        lastRender += Gdx.graphics.getDeltaTime();
        if(lastRender >= frameRate){
            stateToSend.clear();
            for (ObjectMap.Entry<String, Player> shipEntry : players) {
                Player player = shipEntry.value;
                player.act(lastRender);
                stateToSend.add(player);
            }
            lastRender = 0;
            String stateJson = json.toJson(stateToSend);
            sendToAllPlayers(stateJson);
        }

    }

    private void sendToAllPlayers(String json) {
        pool.execute(()->{
            for (StandardWebSocketSession session : webSocketHandler.getSessions()) {
                try {
                    if(session.isOpen()) {
                        session.getNativeSession().getBasicRemote().sendText(json);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
