package com.lofisoftware.robotarena.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.Assets;
import com.lofisoftware.robotarena.RobotArena;

public class RobotArenaMenu extends AbstractScreen {

	private float screenWidth, screenHeight;
	private float buttonWidth, buttonHeight, buttonX;
	private float buttonYmenu, buttonYhelp, buttonYexit, buttonYresume, buttonYcredits;

	Button newGame, resumeGame, help, credits, exit; 
	
	boolean touched = false;
	Vector3 touch = new Vector3();
	InputAdapter input;
	
	public RobotArenaMenu(){
		
		updateSizes();
		newGame = new Button(buttonX,buttonYmenu,buttonWidth,buttonHeight,"New Game",Assets.GameSprite.BUTTON_GRAY_MID.getTexture(),Assets.GameSprite.WHITE.getTexture());
		resumeGame = new Button(buttonX,buttonYresume,buttonWidth,buttonHeight,"Resume Game",Assets.GameSprite.BUTTON_GRAY_MID.getTexture(),Assets.GameSprite.WHITE.getTexture());
		help = new Button(buttonX,buttonYhelp,buttonWidth,buttonHeight,"Help",Assets.GameSprite.BUTTON_GRAY_MID.getTexture(),Assets.GameSprite.WHITE.getTexture());
		credits = new Button(buttonX,buttonYcredits,buttonWidth,buttonHeight,"Credits",Assets.GameSprite.BUTTON_GRAY_MID.getTexture(),Assets.GameSprite.WHITE.getTexture());
		exit = new Button(buttonX,buttonYexit,buttonWidth,buttonHeight,"Exit",Assets.GameSprite.BUTTON_GRAY_MID.getTexture(),Assets.GameSprite.WHITE.getTexture()); 
		
		input = new InputAdapter () {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
				case Input.Keys.ENTER:
				case Input.Keys.N:
					RobotArena.changeGameState(RobotArena.GAME_PLAY);
					break;
				case Input.Keys.R: 
					RobotArena.changeGameState(RobotArena.GAME_RESUME);
					break;	
				case Input.Keys.H: 
					RobotArena.changeGameState(RobotArena.GAME_HELP);
					break;	
				case Input.Keys.C: 
					RobotArena.changeGameState(RobotArena.GAME_CREDITS);
					break;			
				case Input.Keys.ESCAPE: 
					RobotArena.changeGameState(RobotArena.GAME_EXIT);
					break;
				}
				return super.keyDown(keycode);
			}

			public boolean touchDown (int x, int y, int pointer, int button) {
				touched = true;
				touch = camera.unproject(new Vector3(x,y,0));
				Gdx.app.log("RobotArenaMenu", "touch down:" + touch.toString());
				return false;
			}
	
			public boolean touchUp (int x, int y, int pointer, int button) {
				touched = false;
				touch = camera.unproject(new Vector3(x,y,0));
				Gdx.app.log("RobotArenaMenu", "touchup:" + touch.toString());
				return true; // return true to indicate the event was handled
			}
		};
		
	}

	
	private void updateSizes() {
		screenWidth = super.viewport.getWorldWidth();
		screenHeight = super.viewport.getWorldHeight();

		buttonWidth = screenWidth/3;
		buttonHeight = screenHeight/8;
		buttonX = screenWidth/3;
		buttonYmenu = buttonHeight*5;
		buttonYresume = buttonHeight*4;	
		buttonYhelp = buttonHeight*3;
		buttonYcredits = buttonHeight*2;	
		buttonYexit = buttonHeight*1;
			
	}


	//private SpriteBatch batch;
    //private Skin skin;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		batch.setShader(Assets.fontShader);
		Assets.akashiFontLarge.setScale(2);
		Assets.akashiFontLarge.draw(batch, "Robot Arena", screenWidth/2 - (Assets.akashiFontLarge.getBounds("Robot Arena").width/2), screenHeight-(screenHeight/6));
		Assets.akashiFontLarge.setScale(1);
		batch.setShader(null);
		
		if (newGame.touch(touch, touched))
			RobotArena.changeGameState(RobotArena.GAME_PLAY);
		
		newGame.draw(batch);
		
		if (resumeGame.touch(touch, touched))
			RobotArena.changeGameState(RobotArena.GAME_RESUME);
		
		resumeGame.draw(batch);
		
		if (help.touch(touch, touched))
			RobotArena.changeGameState(RobotArena.GAME_HELP);
		
		help.draw(batch);
		
		if (credits.touch(touch, touched))
			RobotArena.changeGameState(RobotArena.GAME_CREDITS);
		
		credits.draw(batch);
		
		if (exit.touch(touch, touched))
			RobotArena.changeGameState(RobotArena.GAME_EXIT);
		
		exit.draw(batch);
		
		batch.end();
		
	}
	
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
		
		updateSizes();
	}


	@Override
	public void show() {
		Gdx.input.setInputProcessor(input);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		Gdx.input.setInputProcessor(input);
		
	}

	@Override
	public void dispose() {
		
	}

}