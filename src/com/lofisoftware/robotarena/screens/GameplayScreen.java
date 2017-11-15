package com.lofisoftware.robotarena.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.lofisoftware.robotarena.RobotArenaGame;

public class GameplayScreen implements Screen {

	private RobotArenaGame game;
	
	public GameplayScreen() {
		game = new RobotArenaGame();
		if (!game.initialize())
			Gdx.app.error("Gameplayscreen","Failed to initialize!");
	}
	
	@Override
	public void show() {


	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		game.update(deltaTime);
		
		/*
		camera.position.x = MathUtils.clamp(camera.position.x, 
											halfWidth * camera.zoom,
											levelTexture.getWidth() * WORLD_TO_SCREEN - halfWidth * camera.zoom);
		camera.position.y = MathUtils.clamp(camera.position.y,
											halfHeight * camera.zoom,
											levelTexture.getHeight() * WORLD_TO_SCREEN - halfHeight * camera.zoom);
		
		// Clamp zoom
		camera.zoom = MathUtils.clamp(camera.zoom, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
		*/
		
		// Log position and zoom
		//Gdx.app.log("position", camera.position.toString());
		//Gdx.app.log("zoom", Float.toString(camera.zoom));
		
		/*
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		// Render the jungle level bottom left corner at (0, 0)
		batch.begin();
		batch.draw(levelTexture,
				   0.0f, 0.0f,
				   0.0f, 0.0f,
				   levelTexture.getWidth(), levelTexture.getHeight(),
				   WORLD_TO_SCREEN, WORLD_TO_SCREEN,
				   0.0f,
				   0, 0,
				   levelTexture.getWidth(), levelTexture.getHeight(),
				   false, false);
		
		batch.end();
		*/

	}

	@Override
	public void pause() {
		game.pause();

	}

	@Override
	public void resume() {
		game.resume();

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void resize(int width, int height) {
		game.updateSize(width, height);
	}

	@Override
	public void dispose() {
		game.dispose();
	}

}
