package com.company.gdx.client;

import com.company.gdx.GameScreen;
import com.company.gdx.GameType;
import com.company.gdx.SpaceWarGame;
import com.company.gdx.client.ws.WsEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class MessageProcessor {
    private final SpaceWarGame spaceWarGame;

    public MessageProcessor(SpaceWarGame spaceWarGame) {
        this.spaceWarGame = spaceWarGame;
    }

    public void processEvent(WsEvent event) {
        String data = event.getData();
        if(data!=null){
            JSONValue parsed = JSONParser.parseStrict(data);
            JSONArray array = parsed.isArray();
            JSONObject object = parsed.isObject();
            if(array != null){
                processArray(array);
            } else if (object != null) {
                processObject(object);
            }
        }
    }

    private void processObject(JSONObject object) {
        JSONValue type = object.get("class");
        if(type != null){
            GameScreen gameScreen = spaceWarGame.getScreenManager().getGameScreen();
            if (gameScreen!=null && gameScreen.getGameType() == GameType.ON_LINE){
                switch (type.isString().stringValue()){
                    case "sessionKey":
                        String playerId = object.get("id").isString().stringValue();
                        gameScreen.setPlayerId(playerId);
                        break;
                    case "evict":
                        String idToEvict = object.get("id").isString().stringValue();
                        gameScreen.deletePlayer(idToEvict);
                        break;
                    case "player":
                        float x = (float) object.get("x").isNumber().doubleValue();
                        float y = (float) object.get("y").isNumber().doubleValue();
                        float angle = (float) object.get("angle").isNumber().doubleValue();
                        int speed = (int) object.get("speed").isNumber().doubleValue();
                        int hp = (int) object.get("hp").isNumber().doubleValue();
                        int hpMax = (int) object.get("hpMax").isNumber().doubleValue();
                        String id = object.get("id").isString().stringValue();
                        gameScreen.updatePlayers(id, x, y, angle, speed, hp, hpMax);
                        break;
                    default:
                        throw new RuntimeException("Unknown object type " + type);
                }
            }
        }
    }

    private void processArray(JSONArray array) {
        for (int i=0; i < array.size();i++){
            JSONValue jsonValue = array.get(i);
            JSONObject object = jsonValue.isObject();
            if (object != null){
                processObject(object);
            }
        }
    }

}
