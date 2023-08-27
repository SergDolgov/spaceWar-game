package com.company.gdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenManager {
    public enum ScreenType {
        MENU, GAME, SETTINGS
    }
    private static final ScreenManager ourInstance = new ScreenManager();
    private SpaceWarGame game;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private SettingsScreen settingsScreen;

    public GameScreen getGameScreen() {
        return gameScreen;
    }
    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private ScreenManager() {
    }

    public void init(SpaceWarGame game, SpriteBatch batch, KeyboardAdapter inputProcessor) {
        this.game = game;
        this.gameScreen = new GameScreen(batch, inputProcessor);
        this.menuScreen = new MenuScreen(batch, inputProcessor);
        this.settingsScreen = new SettingsScreen(batch, inputProcessor);
    }

    public void setScreen(ScreenType screenType, Object... args) {
        Screen currentScreen = game.getScreen();
        switch (screenType) {
            case MENU:
                game.setScreen(menuScreen);
                break;
            case GAME:
                gameScreen.setGameType((GameType) args[0]);
                game.setScreen(gameScreen);
                break;
            case SETTINGS:
                game.setScreen(settingsScreen);
                break;
        }
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
