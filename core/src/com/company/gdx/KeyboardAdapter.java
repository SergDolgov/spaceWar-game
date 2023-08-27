package com.company.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class KeyboardAdapter extends InputAdapter {

    private final Vector2 mousePos = new Vector2();
    private final Vector2 direction = new Vector2();
    private final Vector2 angle = new Vector2(1,1);
    private final InputState inputState;

    public KeyboardAdapter(InputState inputState) {
        this.inputState = inputState;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputState.setFirePressed(true);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputState.setFirePressed(false);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) inputState.setLeftPressed(true);
        if (keycode == Input.Keys.D) inputState.setRightPressed(true);
        if (keycode == Input.Keys.W) inputState.setUpPressed(true);
        if (keycode == Input.Keys.S) inputState.setDownPressed(true);

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.A) inputState.setLeftPressed(false);
        if (keycode == Input.Keys.D) inputState.setRightPressed(false);
        if (keycode == Input.Keys.W) inputState.setUpPressed(false);
        if (keycode == Input.Keys.S) inputState.setDownPressed(false);

        return false;
    }

    public void updateMousePos(){
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();
        mousePos.set(x, y);
    }
    public Vector2 getDirection() {

        direction.set(0, 0);

        if (inputState.isLeftPressed()) direction.add(-1, 0);
        if (inputState.isRightPressed()) direction.add(1, 0);
        if (inputState.isUpPressed()) direction.add(0, 1);
        if (inputState.isDownPressed()) direction.add(0, -1);

        return direction;
    }

    public Vector2 getMousePos() {
        updateMousePos();
        return mousePos;
    }

    public InputState updateAndGetInputState(Vector2 playerOrigin, int speed, int hp, int hpMax){
        updateMousePos();
        angle.set(mousePos).sub(playerOrigin);
        inputState.setAngle(angle.angleDeg());
        inputState.setSpeed(speed);
        inputState.setHp(hp);
        inputState.setHpMax(hpMax);
        return inputState;
    }
}
