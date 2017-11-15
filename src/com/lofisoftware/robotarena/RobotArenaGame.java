package com.lofisoftware.robotarena;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.systems.AnimationSystem;
import com.lofisoftware.robotarena.systems.BoundsSystem;
import com.lofisoftware.robotarena.systems.CameraSystem;
import com.lofisoftware.robotarena.systems.CollisionSystem;
import com.lofisoftware.robotarena.systems.CompetitorSystem;
import com.lofisoftware.robotarena.systems.DamageSystem;
import com.lofisoftware.robotarena.systems.EnemyBrainSystem;
import com.lofisoftware.robotarena.systems.EntityRemovalSystem;
import com.lofisoftware.robotarena.systems.EnvironmentEffectsSystem;
import com.lofisoftware.robotarena.systems.FieldOfViewSystem;
import com.lofisoftware.robotarena.systems.GrimReaperSystem;
import com.lofisoftware.robotarena.systems.HazardSystem;
import com.lofisoftware.robotarena.systems.ItemSystem;
import com.lofisoftware.robotarena.systems.MovementSystem;
import com.lofisoftware.robotarena.systems.OpponentBrainSystem;
import com.lofisoftware.robotarena.systems.PlayerInputSystem;
import com.lofisoftware.robotarena.systems.RenderingSystem;
import com.lofisoftware.robotarena.systems.RepairSystem;
import com.lofisoftware.robotarena.systems.ScoreSystem;
import com.lofisoftware.robotarena.systems.TerminalSystem;
import com.lofisoftware.robotarena.systems.TextureColorSystem;
import com.lofisoftware.robotarena.systems.TurnSystem;
import com.lofisoftware.robotarena.systems.ZoneSystem;

public class RobotArenaGame {
	
	/*
	protected final float CAMERA_SPEED = 2.0f;
	protected final float CAMERA_ZOOM_SPEED = 2.0f;
	protected final float CAMERA_ZOOM_MAX = 1.0f;
	protected final float CAMERA_ZOOM_MIN = 0.01f;
	protected final float CAMERA_MOVE_EDGE = 0.2f;
	*/
	
	private PooledEngine engine;
	private GameEngine gameEngine;
	private RenderingSystem renderingSystem;
	PlayerInputSystem playerInput;
	
	//private Vector3 touch;
	//CollisionListener collisionListener;
	//PerformanceCounter counter;
	
	public RobotArenaGame() {
		//counter = new PerformanceCounter("RobotArenaGame");
	}
	
	public boolean initialize(){
		
		engine = new PooledEngine(100,1000,1000,10000);
		gameEngine = new GameEngine(engine);
		
		FieldOfViewSystem fovSystem = new FieldOfViewSystem(engine);
		ZoneSystem zoneSystem = new ZoneSystem(gameEngine);
		RepairSystem repairSystem = new RepairSystem();
		DamageSystem damageSystem = new DamageSystem();
		EnvironmentEffectsSystem environmentEffectsSystem = new EnvironmentEffectsSystem(engine);
		ItemSystem itemSystem = new ItemSystem();
		CompetitorSystem competitorSystem = new CompetitorSystem(engine);
		HazardSystem hazardSystem = new HazardSystem();
		GrimReaperSystem grimReaperSystem = new GrimReaperSystem(engine);
		TurnSystem turnSystem = new TurnSystem();
		renderingSystem = new RenderingSystem(engine);
		TerminalSystem terminalSystem = new TerminalSystem();
		ScoreSystem scoreSystem = new ScoreSystem();
		EnemyBrainSystem enemyBrainSystem = new EnemyBrainSystem();
		OpponentBrainSystem opponentBrainSystem = new OpponentBrainSystem();
		playerInput = new PlayerInputSystem(engine);
		Gdx.input.setInputProcessor(playerInput);
		
		engine.addSystem(playerInput);
		engine.addSystem(opponentBrainSystem);
		engine.addSystem(enemyBrainSystem);
		engine.addSystem(competitorSystem);
		engine.addSystem(terminalSystem);
		engine.addSystem(new CollisionSystem(gameEngine));
		
		engine.addSystem(new MovementSystem());
		engine.addSystem(new BoundsSystem());
		engine.addSystem(fovSystem);
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new TextureColorSystem());
		engine.addSystem(new CameraSystem());
		engine.addSystem(renderingSystem);
		
		//engine.addSystem(grimReaperSystem);
		engine.addSystem(zoneSystem);
		engine.addSystem(turnSystem);
		engine.addSystem(itemSystem);
		engine.addSystem(hazardSystem);
		
		engine.addSystem(new EntityRemovalSystem(engine));
		
		MessageDispatcher.getInstance().addListeners(terminalSystem, Messages.GAME_OVER, Messages.FINISHED, Messages.ENTITY_PUSHED, Messages.ENTITY_REPAIRED, Messages.ENTITY_DAMAGED, Messages.ENTITY_DIED, Messages.TURN_COMPLETE, Messages.GAME_STARTED, Messages.ENTITY_MOVED, Messages.ZONE_CHANGE, Messages.ENTITY_COLLISION);
		MessageDispatcher.getInstance().addListeners(scoreSystem, Messages.GAME_OVER, Messages.FINISHED, Messages.ENTITY_PUSHED, Messages.ENTITY_REPAIRED, Messages.ENTITY_DAMAGED, Messages.ENTITY_DIED, Messages.TURN_COMPLETE, Messages.GAME_STARTED, Messages.ENTITY_MOVED, Messages.ZONE_CHANGE, Messages.ENTITY_COLLISION);
		MessageDispatcher.getInstance().addListeners(fovSystem, Messages.GAME_STARTED, Messages.ENTITY_MOVED, Messages.ZONE_CHANGE);
		MessageDispatcher.getInstance().addListeners(turnSystem, Messages.GAME_OVER, Messages.TURN_COMPLETE);
		MessageDispatcher.getInstance().addListener(zoneSystem, Messages.ENTITY_MOVED);
		MessageDispatcher.getInstance().addListeners(repairSystem, Messages.REPAIR);
		MessageDispatcher.getInstance().addListeners(damageSystem, Messages.ENTITY_COLLISION, Messages.DAMAGE);
		MessageDispatcher.getInstance().addListener(grimReaperSystem, Messages.ENTITY_DAMAGED);
		MessageDispatcher.getInstance().addListeners(gameEngine, Messages.ZONE_CHANGE);
		MessageDispatcher.getInstance().addListeners(playerInput, Messages.ZONE_CHANGE, Messages.GAME_STARTED, Messages.TURN_COMPLETE);
		MessageDispatcher.getInstance().addListeners(itemSystem, Messages.ROUND_COMPLETE);
		MessageDispatcher.getInstance().addListeners(hazardSystem, Messages.ROUND_COMPLETE);
		MessageDispatcher.getInstance().addListeners(environmentEffectsSystem, Messages.PUSH);
		MessageDispatcher.getInstance().addListeners(competitorSystem, Messages.TURN_COMPLETE);
		
		gameEngine.create();
		
		updateSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		MessageDispatcher.getInstance().dispatchMessage(null, Messages.GAME_STARTED);
		
		return true;
	}

	public void setSystemProcessing(boolean processing) {
		engine.getSystem(FieldOfViewSystem.class).setProcessing(processing);
		engine.getSystem(ZoneSystem.class).setProcessing(processing);
		//engine.getSystem(RepairSystem.class).setProcessing(processing);
		//engine.getSystem(DamageSystem.class).setProcessing(processing);
		//engine.getSystem(EnvironmentEffectsSystem.class).setProcessing(processing);
		engine.getSystem(ItemSystem.class).setProcessing(processing);
		engine.getSystem(CompetitorSystem.class).setProcessing(processing);
		engine.getSystem(HazardSystem.class).setProcessing(processing);
		//engine.getSystem(GrimReaperSystem.class).setProcessing(processing);
		engine.getSystem(TurnSystem.class).setProcessing(processing);
		engine.getSystem(RenderingSystem.class).setProcessing(processing);
		engine.getSystem(TerminalSystem.class).setProcessing(processing);
		//engine.getSystem(ScoreSystem.class).setProcessing(processing);
		engine.getSystem(EnemyBrainSystem.class).setProcessing(processing);
		engine.getSystem(OpponentBrainSystem.class).setProcessing(processing);
		engine.getSystem(PlayerInputSystem.class).setProcessing(processing);
		engine.getSystem(CollisionSystem.class).setProcessing(processing);
		engine.getSystem(MovementSystem.class).setProcessing(processing);
		engine.getSystem(BoundsSystem.class).setProcessing(processing);
		engine.getSystem(AnimationSystem.class).setProcessing(processing);
		engine.getSystem(TextureColorSystem.class).setProcessing(processing);
		engine.getSystem(CameraSystem.class).setProcessing(processing);
		engine.getSystem(EntityRemovalSystem.class).setProcessing(processing);		
	}
	
	public void update(float deltaTime) {
		//checkInput(deltaTime);
		//counter.tick(deltaTime);
		//counter.start();
		engine.update(deltaTime);
		MessageDispatcher.getInstance().update(deltaTime);
		//counter.stop();
		//Gdx.app.log("RobotArena:update", "Counter load: " + counter.toString());
		
	}
	/*
	private void checkInput(float deltaTime) {
		// Arrow keys move the camera
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			camera.position.x -= CAMERA_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			camera.position.x += CAMERA_SPEED * deltaTime;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP)) {
			camera.position.y += CAMERA_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			camera.position.y -= CAMERA_SPEED * deltaTime;
		}
		
		// Touching on the edges also moves the camera
		if (Gdx.input.isTouched()) {
			touch.set(Gdx.input.getX(), Gdx.input.getY(), 0.0f);
			camera.unproject(touch);
			
			if (touch.x > SCENE_WIDTH * (1.0f - CAMERA_MOVE_EDGE)) {
				camera.position.x += CAMERA_SPEED * deltaTime;
			}
			else if (touch.x < SCENE_WIDTH * CAMERA_MOVE_EDGE) {
				camera.position.x -= CAMERA_SPEED * deltaTime;
			}
			
			if (touch.y > SCENE_HEIGHT * (1.0f - CAMERA_MOVE_EDGE)) {
				camera.position.y += CAMERA_SPEED * deltaTime;
			}
			else if (touch.y < SCENE_HEIGHT * CAMERA_MOVE_EDGE) {
				camera.position.y -= CAMERA_SPEED * deltaTime;
			}
		}
		
		// Page up/down control the zoom
		if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom -= CAMERA_ZOOM_SPEED * deltaTime;
		}
		else if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom += CAMERA_ZOOM_SPEED * deltaTime;
		}
		
	}
	*/

	public void updateSize(int width, int height) {
		if (renderingSystem != null)
			renderingSystem.updateSize(width, height);
		
	}

	public void dispose() {
		if (renderingSystem != null)
			renderingSystem.dispose();
		
	}

	public void pause() {
		setSystemProcessing(false);
		Gdx.input.setInputProcessor(playerInput);
		
	}

	public void resume() {
		setSystemProcessing(true);
		Gdx.input.setInputProcessor(playerInput);
		
	}
}
