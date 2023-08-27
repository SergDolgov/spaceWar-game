package com.company.gdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import static com.company.gdx.Constants.*;

public abstract class MovableObject {
    private final int cellSize;
    private final float halfSize;
    private final Vector2 position = new Vector2();
    private final Vector2 angle = new Vector2(1,1);
    private final Vector2 origin = new Vector2();
    private final Texture texture;
    private TextureRegion textureRegion;
    private int speed;
    private boolean active = false;

    public MovableObject(String textureName, int cellSize) {
        texture = new Texture(textureName);
        textureRegion = new TextureRegion(texture);
        this.cellSize = cellSize;
        this.halfSize = cellSize / 2;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public boolean isActive() {return active;}

    public Vector2 getAngle() {
        return angle;
    }

    public void deactivate() {setActive(false);}

    public void setActive(boolean active) {
        this.active = active;
    }

    public void activate(float x, float y, int speed) {
        position.set(x, y);
        origin.set(position).add(halfSize, halfSize);
        this.speed = speed;
        this.active = true;
    }

    public void render(Batch batch) {
            batch.draw(
                    textureRegion,
                    position.x, position.y,
                    halfSize, halfSize,
                    cellSize, cellSize, 1, 1,
                    angle.angleDeg() - 90);
    }

    public void dispose() {texture.dispose();}

    public void moveTo(Vector2 direction, float delta) {
        if(isActive()) {
            if (notCanMove()) updatePosition();
            position.add(direction.x * speed * delta, direction.y * speed * delta);
            origin.set(position).add(halfSize, halfSize);
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void rotateTo(Vector2 rotateTo) {angle.set(rotateTo).sub(origin);}

    public Vector2 getPosition() {return position;}

    public Vector2 getOrigin() {
        return origin;
    }

    public void setPosition(Vector2 newPosition) {
        position.set(newPosition);
        origin.set(position).add(halfSize, halfSize);
    }

    public int getCellSize() {
        return cellSize;
    }

    public boolean notCanMove() {
        return  !(position.x > 0 && position.x < WORLD_WIDTH && position.y > cellSize && position.y < WORLD_HEIGHT);
    }

    public void updatePosition(){
        if (position.x <= 0) position.x = 1;
        if (position.y <= cellSize) position.y = cellSize + 1;
        if (position.x >= WORLD_WIDTH) position.x = WORLD_WIDTH - 1;
        if (position.y >= WORLD_HEIGHT) position.y = WORLD_HEIGHT - 1;
        origin.set(position).add(halfSize, halfSize);
    }

    public void moveTo(float x, float y) {
//        if (isActive()) {
//            if (notCanMove()) updatePosition();
            position.set(x, y);
            origin.set(position).add(halfSize, halfSize);
 //       }
    }
    public void rotateTo(float angle) {
        this.angle.setAngleDeg(angle);
    }
}
