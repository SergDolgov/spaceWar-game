package com.company.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.ArrayList;
import java.util.List;

import static com.company.gdx.Constants.*;
import static com.company.gdx.ShipOwner.*;

public class GameScreen extends AbstractScreen {
    private SpriteBatch batch;
    private GameType gameType;
    private final static int MAX_ENEMIES = 12;
    private final static int MAX_BULLETS = 300;
    private final static int MAX_ITEMS = 10;
    private Ship playerShip;
    private String playerId;
    private MessageSender messageSender;

    public ObjectMap<String, Ship> getPlayers() {
        return players;
    }

    private final ObjectMap<String, Ship> players = new ObjectMap<>();
    private final List<EnemyShip> enemies = new ArrayList<>(MAX_ENEMIES);
    private final List<Bullet> bullets = new ArrayList<>(MAX_BULLETS);
    private final List<Item> items = new ArrayList<>(MAX_ITEMS);
    private final KeyboardAdapter inputProcessor;
    private Texture screenTexture;
    private BitmapFont font;
    private String text;
    private boolean paused;
    private float gameTimer;
    private float worldTimer;

    public GameScreen(SpriteBatch batch, KeyboardAdapter inputProcessor) {
        this.batch = batch;
        this.inputProcessor = inputProcessor;
        this.gameType = GameType.SINGLE;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public void show () {

        screenTexture = new Texture("space.jpg");

        Gdx.input.setInputProcessor(inputProcessor);
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        gameTimer = 100.0f;

        initCollections();

        text = "Game state";

    }

    private void initCollections() {
        if(gameType == GameType.SINGLE){
            initPlayer();
        }
        for (int i = 0; i < MAX_ENEMIES; i++) enemies.add(new EnemyShip("spaceship1.png", CELL_SIZE, ShipOwner.AI));
        for (int i = 0; i < MAX_BULLETS;i++) bullets.add(new Bullet());
        for (int i = 0; i < MAX_ITEMS; i ++) items.add(new Item());
    }

    public Ship getPlayerShip() {
        return playerShip;
    }

    public String getPlayerId() {
        return playerId;
    }

    private void initPlayer() {
        playerShip = new Ship("spaceship2.png", CELL_SIZE, ShipOwner.PLAYER);
        playerShip.activate(0, 0, 180);
//        InputState playerState = inputProcessor.updateAndGetInputState(playerShip.getOrigin(), 180);
//        messageSender.sendMessage(playerState);
        playerShip.weapon.setType(Weapon.Type.MEDIUM);
       // playerShip.setHp(20);
        if(gameType == GameType.ON_LINE){
            players.put(playerId, playerShip);
        }
    }

    public GameType getGameType() {
        return gameType;
    }

    @Override
    public void render (float delta) {

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) paused = !paused;

        if (paused) {
            //ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.MENU);
            return;
        }

        update(delta);

        if (Gdx.input.isTouched()) fire(playerShip);

        batch.begin();
        batch.draw(screenTexture,0,0);

        renderAll(batch, delta);

        //text = "Current Hp = " + playerShip.hp;
        text = "Current Hp = " + players.size + "; " + playerId + ";"+playerShip;

        font.draw(batch, gameType.toString() ,30, 630);
        font.draw(batch, Gdx.graphics.getWidth() + ";" + Gdx.graphics.getHeight() ,30, 610);
        font.draw(batch, text ,30, 590);
        int i = 1;
        for (String key : players.keys()){
            text = "p"+ key + "; " + players.get(key).getPosition();
            font.draw(batch, text ,30, 590 - 20 * i);
            i++;
        }

        batch.end();
    }

    private void renderAll(Batch batch, float delta){

        renderBullets(batch);
        renderEnemies(batch, delta);
        renderItems(batch);

        if(gameType == GameType.ON_LINE){
            for (String key : players.keys()) {
                //players.get(key).render(batch);
                renderPlayer(players.get(key), batch, delta);
            }
        }
        else {
            renderPlayer(playerShip, batch, delta);
        }
    }

    private void renderItems(Batch batch) {
        items.forEach(item -> {
            if (item.isActive()){
                item.moveTo();
                item.render(batch);
            }
        });
    }

    private void renderBullets(Batch batch) {
        bullets.forEach(bullet -> {
            if (bullet.isActive()){
                bullet.moveTo();
                bullet.render(batch);
            }
        });
    }

    private void renderEnemies(Batch batch, float delta) {
        for (EnemyShip enemy : enemies)
            if(enemy.isActive()){
                for (Bullet bullet : bullets)
                    if (bullet.isActive() && checkIntersection(bullet, enemy)) {
                        bullet.deactivate();
                        enemy.takeDamage(bullet.getDamage());
                        break;
                    }
                if (!enemy.isDestroyed()) {
                    enemy.moveTo(delta);
                    if (playerShip != null) {
                        enemy.rotateTo(playerShip.getOrigin());
                        if (enemy.getOrigin().dst(playerShip.getOrigin()) < enemy.getPursuilRadius()) {
                            fire(enemy);
                        }
                    }
                }
                enemy.render(batch);
            }
    }

    private void renderPlayer(Ship player, Batch batch, float delta) {

        if (!player.isActive()) {
            player.setActive(true);
            player.setHp(12);
            player.setHpMax(12);
        }

        if(gameType == GameType.SINGLE) {
            player.moveTo(inputProcessor.getDirection(), delta);
            player.rotateTo(inputProcessor.getMousePos());
        }


        player.render(batch);

        for (Bullet bullet : bullets)
            if (bullet.isActive() && checkIntersection(bullet, player)) {
                bullet.deactivate();
                player.takeDamage(bullet.getDamage());
                break;
            }
        for (Item item : items)
            if (item.isActive() && checkIntersection(item, player)) {
                item.deactivate();
                player.setHp(item.getDamage());
                break;
            }

    }

    private void fire(Ship ship){
        for (Bullet bullet : bullets)
            if (!bullet.isActive()){
                ship.fire(bullet);
                return;
            }
    }

    public static boolean checkIntersection(MovableObject obj1, MovableObject obj2) {

        if (((obj1 instanceof Bullet  && !(obj1 instanceof Item)) &&( obj2 instanceof Ship)
                &&((Bullet) obj1).getOwner().getOwnerType()==((Ship) obj2).getOwnerType())
            )
            return false;

        Vector2 posObj1 = obj1.getPosition();
        Vector2 posObj2 = obj2.getPosition();
        int sizeObj1 = obj1.getCellSize();
        int sizeObj2 = obj2.getCellSize();

        int xMaxObj1 = (int)posObj1.x + sizeObj1;
        int yMaxObj1 = (int)posObj1.y + sizeObj1;
        int xMaxObj2 = (int)posObj2.x + sizeObj2;
        int yMaxObj2 = (int)posObj2.y + sizeObj2;

        return !(posObj1.x > xMaxObj2 || posObj2.x > xMaxObj1 || posObj1.y > yMaxObj2 || posObj2.y > yMaxObj1);
    }

    public void update(float dt) {
        worldTimer += dt;
        if (!paused) {
            gameTimer += dt;
            if (gameTimer > 15.0f) {
                gameTimer = 0;
                generateRandomEnemies();
                generateRandomItem(MathUtils.random(4), MathUtils.random(0.5f));
            }
        }
    }

    private void generateRandomEnemies(){
        for (int i = 0; i < 5;i++)
            for (EnemyShip enemy : enemies)
                if(!enemy.isActive()) {
                    int x = MathUtils.random(WORLD_WIDTH);
                    int y = WORLD_HEIGHT;
                    int speed = MathUtils.random(100, 150);
                    enemy.activate(x, y, speed);
                    break;
                }
    }

    public void generateRandomItem(int count, float probability) {
        for (int q = 0; q < count; q++) {
            float n = MathUtils.random(0.0f, 1.0f);
            if (n <= probability) {
                int type = MathUtils.random(0, Item.Type.values().length - 1);
                for (Item item : items) {
                    if (!item.isActive()) {
                        int x = MathUtils.random(WORLD_WIDTH);
                        int y = MathUtils.random(WORLD_HEIGHT);
                        item.activate(x, y, Item.Type.values()[type]);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void dispose () {
        batch.dispose();
        playerShip.dispose();
        for (Ship value : players.values()) {
            value.dispose();
        }
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
        initPlayer();
    }

    public void deletePlayer(String idToEvict) {
        players.remove(idToEvict);
    }

    public void updatePlayers(String id, float x, float y, float angle, int speed, int hp, int hpMax) {
        if(playerId == null && speed == 0) {
            playerId = id;
            initPlayer();
            return;
        }
        if (players.isEmpty()){
            return;
        }
        Ship ship = players.get(id);
        if (ship == null){
            ship = new Ship("spaceship5.png", CELL_SIZE, REMOTE);
            ship.activate(x, y, speed);
            ship.setHp(hp);
            ship.setHpMax(hpMax);
            players.put(id, ship);
        }else {
            ship.moveTo(x, y);
            ship.rotateTo(angle);
            ship.setHp(hp);
            ship.setHpMax(hpMax);
        }

    }

}
