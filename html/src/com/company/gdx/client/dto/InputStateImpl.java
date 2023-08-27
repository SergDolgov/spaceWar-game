package com.company.gdx.client.dto;

import com.company.gdx.InputState;

public class InputStateImpl implements InputState {

    public InputStateImpl() {
        setType("state");
        setRightPressed(false);
        setLeftPressed(false);
        setUpPressed(false);
        setDownPressed(false);
        setFirePressed(false);
        setAngle(0);
        setSpeed(0);
        setHp(0);
        setHpMax(0);
    }
    public InputStateImpl(boolean leftPressed, boolean rightPressed, boolean upPressed, boolean downPressed, boolean firePressed,
                          float angle, int speed, int hp, int hpMax) {
        this();
        setRightPressed(rightPressed);
        setLeftPressed(leftPressed);
        setUpPressed(upPressed);
        setDownPressed(downPressed);
        setFirePressed(firePressed);
        setAngle(angle);
        setSpeed(speed);
        setHp(hp);
        setHpMax(hpMax);
    }

    public native String getType() /*-{
        return this.type;
    }-*/;

    public native void setType(String type) /*-{
        this.type = type;
    }-*/;

    @Override
    public native boolean isLeftPressed() /*-{
        return this.leftPressed;
    }-*/;

    @Override
    public native void setLeftPressed(boolean leftPressed) /*-{
        this.leftPressed = leftPressed;
    }-*/;

    @Override
    public native boolean isRightPressed() /*-{
        return this.rightPressed;
    }-*/;

    @Override
    public native void setRightPressed(boolean rightPressed) /*-{
        this.rightPressed = rightPressed;
    }-*/;

    @Override
    public native boolean isUpPressed() /*-{
        return this.upPressed;
    }-*/;

    @Override
    public native void setUpPressed(boolean upPressed) /*-{
        this.upPressed = upPressed;
    }-*/;

    @Override
    public native boolean isDownPressed() /*-{
        return this.downPressed;
    }-*/;

    @Override
    public native void setDownPressed(boolean downPressed) /*-{
        this.downPressed = downPressed;
    }-*/;

    @Override
    public native boolean isFirePressed() /*-{
        return this.firePressed;
    }-*/;

    @Override
    public native void setFirePressed(boolean firePressed) /*-{
        this.firePressed = firePressed;
    }-*/;
    
    @Override
    public native float getAngle() /*-{
        return this.angle;
    }-*/;
    
    @Override
    public native void setAngle(float angle) /*-{
        this.angle = angle;
    }-*/;

    @Override
    public native int getSpeed() /*-{
        return this.speed;
    }-*/;

    @Override
    public native void setSpeed(int speed)  /*-{
        this.speed = speed;
    }-*/;

    @Override
    public native int getHp() /*-{
        return this.hp;
    }-*/;

    @Override
    public native void setHp(int hp) /*-{
        this.hp = hp;
    }-*/;

    @Override
    public native int  getHpMax()  /*-{
        return this.hpMax;
    }-*/;

    @Override
    public native void  setHpMax(int hpMax) /*-{
        this.hpMax = hpMax;
    }-*/;
}
