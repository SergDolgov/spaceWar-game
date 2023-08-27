package com.company.gdx;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SpaceWarGame extends Game {
	private SpriteBatch batch;
	private final KeyboardAdapter inputProcessor;
	private MessageSender messageSender;
	private final ScreenManager screenManager;


	public ScreenManager getScreenManager() {
		return screenManager;
	}

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	public SpaceWarGame(InputState inputState) {
		this.inputProcessor = new KeyboardAdapter(inputState);
		this.screenManager = ScreenManager.getInstance();
	}

	@Override
	public void create() {
		batch = new SpriteBatch();
		screenManager.init(this, batch, inputProcessor);
		screenManager.setScreen(ScreenManager.ScreenType.MENU);
	}

	@Override
	public void render() {
		float dt = Gdx.graphics.getDeltaTime();
		getScreen().render(dt);
	}

	@Override
	public void dispose() {
		batch.dispose();
		}

	public void handleTimer() {

		GameScreen gameScreen = screenManager.getGameScreen();
		if (gameScreen!=null && gameScreen.getGameType() == GameType.ON_LINE && !gameScreen.getPlayers().isEmpty()) {
			String playerId = gameScreen.getPlayerId();
			if(playerId != "") {
				Ship player = gameScreen.getPlayers().get(playerId);
				if (player != null) {
					InputState playerState = inputProcessor.updateAndGetInputState(
							player.getOrigin(),
							player.getSpeed(),
							player.getHp(),
							player.getHpMax());
					messageSender.sendMessage(playerState);
				}
			}
		}
	}

}
