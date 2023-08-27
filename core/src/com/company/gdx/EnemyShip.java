package com.company.gdx;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class EnemyShip extends Ship{
    private Direction direction;
    private int timeMoved;
    private final float pursuilRadius;
    public EnemyShip(String textureName, int cellSize, ShipOwner ownerType) {
        super(textureName, cellSize, ownerType);
        pursuilRadius = 300.0f;
    }

    @Override
    public void activate(float x, float y, int speed) {
        super.activate(x, y, speed);
        timeMoved = 50;
        direction = Direction.DOWN;
    }

    public float getPursuilRadius() {
        return pursuilRadius;
    }

    public void moveTo(float delta) {
        if (notCanMove()) timeMoved = 0;
        if (timeMoved > 0) timeMoved --;
        else{
            timeMoved = MathUtils.random(50, 150);
            direction = Direction.values()[MathUtils.random(0, Direction.values().length - 1)];
        }
        moveTo(new Vector2(direction.getVx(), direction.getVy()), delta);
    }
}
