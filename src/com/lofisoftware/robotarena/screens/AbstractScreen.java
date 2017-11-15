package com.lofisoftware.robotarena.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class AbstractScreen implements Screen {

	//protected final float WORLD_TO_SCREEN = 1.0f / 15.0f;
	protected final float SCENE_WIDTH = 768;
	protected final float SCENE_HEIGHT = 1024;;
	
	protected OrthographicCamera camera;
	protected Viewport viewport;
	protected SpriteBatch batch;
	
	public AbstractScreen() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, camera);
		batch = new SpriteBatch();
		//camera.position.x = SCENE_WIDTH * 0.5f;
		//camera.position.y = SCENE_HEIGHT * 0.5f;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

}
