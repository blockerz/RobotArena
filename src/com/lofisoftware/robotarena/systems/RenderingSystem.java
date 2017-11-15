package com.lofisoftware.robotarena.systems;

import java.util.Comparator;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lofisoftware.robotarena.Assets;
import com.lofisoftware.robotarena.RobotArena;
import com.lofisoftware.robotarena.commands.Command;
import com.lofisoftware.robotarena.commands.DoNothingCommand;
import com.lofisoftware.robotarena.commands.MoveCommand;
import com.lofisoftware.robotarena.components.ActiveZoneComponent;
import com.lofisoftware.robotarena.components.CommandComponent;
import com.lofisoftware.robotarena.components.TextureComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;
import com.lofisoftware.robotarena.tween.VectorAccessor;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;
import com.lofisoftware.robotarena.world.FieldOfView;
import com.lofisoftware.robotarena.world.Map;
import com.lofisoftware.robotarena.world.Tile;


public class RenderingSystem extends IteratingSystem {

	public static final float SCENE_WIDTH = 16f;
	public static final float SCENE_HEIGHT = 16f * 1.333f;
	//public static final float SCENE_WIDTH = 15f;
	//public static final float SCENE_HEIGHT = 15f;
	
	static final float MAP_TILE_WIDTH = 15f;
	static final float MAP_TILE_HEIGHT = 15f;
	
	static final float PIXELS_TO_METRES = 1.0f / 8.0f;
	static final float OFFSETX = 0f;
	static final float OFFSETY = 3f;
	
	static final float GUI_SCALE = 24f;
	static final float MESSAGE_WIDTH = 10f;
	static final float MESSAGE_HEIGHT = 4f;
	static final float MESSAGE_ORIGIN = 9f;
	static final float MESSAGE_DISPLAY = 8f;
	static final int MESSAGE_ROWS = 7;
	
	public static int BUTTON_MOVE_EAST_X = 27;
	public static int BUTTON_MOVE_EAST_Y = 3;
	public static int BUTTON_MOVE_SOUTH_X = 25;
	public static int BUTTON_MOVE_SOUTH_Y = 1;
	public static int BUTTON_MOVE_WEST_X = 23;
	public static int BUTTON_MOVE_WEST_Y = 3;
	public static int BUTTON_MOVE_NORTH_X = 25;
	public static int BUTTON_MOVE_NORTH_Y = 5;
	public static int BUTTON_WAIT_X = 27;
	public static int BUTTON_WAIT_Y = 8;
	public static int BUTTON_BACK_X = 23;
	public static int BUTTON_BACK_Y = 8;
	public static int BUTTON_ENTER_X = 17;
	public static int BUTTON_ENTER_Y = 9;
	public static int BUTTON_ENTER_TEXT_X = 18;
	public static int BUTTON_ENTER_TEXT_Y = 11;
	public static float REGISTERS_ORIGIN_X = 1;
	public static float REGISTERS_ORIGIN_Y = 9.5f;
	
	public static float PLAYER1_ORIGIN_X = 1.0f;
	public static float PLAYER1_ORIGIN_Y = 41.7f;
	public static float FINISH_ORIGIN_X = 2;
	public static float FINISH_ORIGIN_Y = 41.7f;
	
	private int screenWidth, screenHeight = 0;
	
	private Array<Entity> renderQueue;
	private Comparator<Entity> comparator;
	private PooledEngine engine;
	
	protected OrthographicCamera gameCamera;
	protected Viewport gameViewport;
	protected SpriteBatch gameBatch;
	private Rectangle gameViewportBounds;
	
	protected OrthographicCamera guiCamera;
	protected Viewport guiViewport;
	protected SpriteBatch guiBatch;
	private Rectangle guiViewportBounds;
	
	private ImmutableArray<Entity> activeZone;
	private Color darkTint;
	private Color litTint,greenTint, redTint;
	//private Vector3 touchPosition;
	private boolean touched;
	//private Line line;
	private Point touchPoint;
	private Rectangle mapArea, guiArea; 
	
	Vector3 toprow = new Vector3(GUI_SCALE,MESSAGE_ORIGIN,0);
	Vector3 displayRow = new Vector3(GUI_SCALE,MESSAGE_DISPLAY,0);
	int messageSize = 0;
	
	@SuppressWarnings("unchecked")
	public RenderingSystem(PooledEngine engine) {
		super(Family.getFor(TransformComponent.class, TextureComponent.class));
		
		renderQueue = new Array<Entity>();
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				return (int)Math.signum(Mappers.transformComponent.get(entityA).pos.z -
						Mappers.transformComponent.get(entityB).pos.z);
			}
		};
		
		
		gameCamera = new OrthographicCamera();
		gameViewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT, gameCamera);
		gameBatch = new SpriteBatch();
		
		guiCamera = new OrthographicCamera();
		guiViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), guiCamera);
		guiBatch = new SpriteBatch();
		
		this.engine = engine;
		
		darkTint = new Color(Color.GRAY);
		litTint = new Color(Color.YELLOW);
		greenTint = new Color(Color.GREEN);
		redTint = new Color(Color.RED);
		
		//touchPosition = new Vector3();
		touchPoint = new Point(0,0);
		
		mapArea = new Rectangle(0,0,0,0); 
		guiArea = new Rectangle(0,0,0,0); 

		
		//Assets.arialFont.setScale(PIXELS_TO_METRES);
		//Assets.akashiFontLarge.setScale(0.5f);
		
		//cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		//cam.position.set(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, 0);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		renderQueue.sort(comparator);
		
		TextureRegion texture;
		
		gameViewport.update(screenWidth, screenHeight);
		
		gameCamera.update();
		gameBatch.setProjectionMatrix(gameCamera.combined);
		
		float width = Tile.TILE_WIDTH;
		float height = Tile.TILE_HEIGHT;
		//float originX = MathUtils.floor(cam.frustum.planePoints[0].x);
		//float originY = MathUtils.floor(cam.frustum.planePoints[0].y);
		
		
		Color tempColor = null;
		
		ZoneComponent activeZoneComponent = activeZone.get(0).getComponent(ZoneComponent.class);
		Entity player = engine.getSystem(PlayerInputSystem.class).getPlayer();
		//Entity player = Mappers.cameraComponent.get(engine.getSystem(CameraSystem.class).getCamera()).target;
		//FieldOfView playerFov = Mappers.fieldOfViewComponent.get(player).fieldOfView.get(activeZoneComponent.zoneID);
		FieldOfView playerFov = Mappers.fieldOfViewComponent.get(Mappers.cameraComponent.get(engine.getSystem(CameraSystem.class).getCamera()).target).fieldOfView.get(activeZoneComponent.zoneID);
		
		mapArea.x = Math.max(0, (gameCamera.position.x - (MAP_TILE_WIDTH/2 + 1)));
		mapArea.y = Math.max(0, (gameCamera.position.y - (MAP_TILE_HEIGHT/2 + 1)));
		mapArea.width = Math.min(activeZoneComponent.zone.getWidth(), (gameCamera.position.x + (MAP_TILE_WIDTH/2 + 1)));
		mapArea.height = Math.min(activeZoneComponent.zone.getHeight(), (gameCamera.position.y + (MAP_TILE_HEIGHT/2 + 1)));
		
		touched = engine.getSystem(PlayerInputSystem.class).isTouched();
		//touchPosition.set(engine.getSystem(PlayerInputSystem.class).getTouchWorldPosition());
		touchPoint.set(MathUtils.floor(engine.getSystem(PlayerInputSystem.class).getTouchWorldPosition().x),MathUtils.floor(engine.getSystem(PlayerInputSystem.class).getTouchWorldPosition().y));
		//if (touched) {
		//	line = engine.getSystem(PlayerInputSystem.class).getPlayerPath();
		//}

		gameBatch.begin();
		
		//for (int x = Math.max(0, (int) (cam.position.x - (FRUSTUM_WIDTH/2 + 1))); x < Math.min(activeZoneComponent.zone.getWidth(), (int) (cam.position.x + (FRUSTUM_WIDTH/2 + 1)));x++)
		//	for (int y = Math.max(0, (int) (cam.position.y - (FRUSTUM_HEIGHT/2 + 1))); y < Math.min(activeZoneComponent.zone.getHeight(), (int) (cam.position.y + (FRUSTUM_HEIGHT/2 + 1)));y++) {
		for (int x = (int) mapArea.x; x < (int) mapArea.width;x++)
			for (int y = (int) mapArea.y; y < mapArea.height;y++) {
				for (int z = 0; z < activeZoneComponent.zone.getLayers(); z++) {
					
					Tile tile = activeZoneComponent.zone.getZoneTile(x, y, z);
					
/*					String region = ""+activeZoneComponent.zone.getMap().getTile(x, y, 0);
					Assets.akashiFontSmall.setScale(1f/24f);
					Assets.akashiFontSmall.draw(gameBatch, region , x,y+OFFSETY);
					Assets.akashiFontSmall.setScale(1f);*/
					
					if (tile.isVisible()) {
						texture = tile.getTextureRegion();		
					
						tempColor = gameBatch.getColor();
						
						if (playerFov.isRemembered(x, y)) {
							if (!playerFov.isVisible(x, y))
								gameBatch.setColor(darkTint);

							gameBatch.draw(texture,
									   x, y,
									   OFFSETX, OFFSETY,
									   texture.getRegionWidth(), texture.getRegionHeight(),
									   PIXELS_TO_METRES, PIXELS_TO_METRES,
									   0);
							gameBatch.setColor(tempColor);
						}
					}
							
				}
			}
		
		for (Entity entity : renderQueue) {
			TextureComponent tex = Mappers.textureComponent.get(entity);
			
			if (tex.region == null || !tex.visible) {
				continue;
			}
			
			TransformComponent t = Mappers.transformComponent.get(entity);
			
			if (t.zoneID != activeZoneComponent.zoneID) {
				continue;
			}

			
			width = tex.region.getRegionWidth();
			height = tex.region.getRegionHeight();
			
			if (playerFov.isVisible(MathUtils.floor(t.newPos.x), MathUtils.floor(t.newPos.y)) ){
					//&& activeZoneComponent.zone.isLit(MathUtils.floor(t.newPos.x), MathUtils.floor(t.newPos.y))) {
				
				tempColor = gameBatch.getColor();
				
				if (tex.tint && tex.tintColor != null)
					gameBatch.setColor(tex.tintColor);
					
				gameBatch.draw(tex.region,
						   t.pos.x + tex.offsetX, t.pos.y + tex.offsetY,
						   OFFSETX, OFFSETY,
						   width, height,
						   t.scale.x * PIXELS_TO_METRES, t.scale.y * PIXELS_TO_METRES,
						   MathUtils.radiansToDegrees * t.rotation);
				
				gameBatch.setColor(tempColor);
			}
			
			if (touched && engine.getSystem(RenderingSystem.class).isPointInGameMapViewport(touchPoint))  {
				texture = Tile.HIGHLIGHT.getTextureRegion();
				
				gameBatch.draw(texture,
						touchPoint.x(), touchPoint.y(),
						   OFFSETX, OFFSETY,
						   texture.getRegionWidth(), texture.getRegionHeight(),
						   PIXELS_TO_METRES, PIXELS_TO_METRES,
						   0);
			}
		}
		
		gameBatch.end();
		renderQueue.clear();
		
		guiViewport.update(screenWidth, screenHeight, true);
		
		guiCamera.update();
		guiBatch.setProjectionMatrix(guiCamera.combined);
		
		guiArea.x = 0;
		guiArea.y = 0;
		guiArea.width = guiViewport.getWorldWidth();
		guiArea.height = guiViewport.getWorldHeight();
		
		guiBatch.begin();
		texture = Assets.GameSprite.GUI.getTexture();
		
		guiBatch.draw(texture,
					0, 0,
				   0, 0,
				   texture.getRegionWidth(), texture.getRegionHeight(),
				   2, 2,
				   0);
		
		texture = Assets.GameSprite.BLACK.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE, GUI_SCALE,
			   0, 0,
			   GUI_SCALE*MESSAGE_WIDTH, GUI_SCALE*MESSAGE_HEIGHT,
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.BUTTON_BLUE_SMALL.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_EAST_X, GUI_SCALE*BUTTON_MOVE_EAST_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_SOUTH_X, GUI_SCALE*BUTTON_MOVE_SOUTH_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_WEST_X, GUI_SCALE*BUTTON_MOVE_WEST_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_NORTH_X, GUI_SCALE*BUTTON_MOVE_NORTH_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_WAIT_X, GUI_SCALE*BUTTON_WAIT_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_BACK_X, GUI_SCALE*BUTTON_BACK_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.ARROW_EAST.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_EAST_X, GUI_SCALE*BUTTON_MOVE_EAST_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.ARROW_SOUTH.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_SOUTH_X, GUI_SCALE*BUTTON_MOVE_SOUTH_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.ARROW_WEST.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_WEST_X, GUI_SCALE*BUTTON_MOVE_WEST_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.ARROW_NORTH.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_MOVE_NORTH_X, GUI_SCALE*BUTTON_MOVE_NORTH_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.CLOCK.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_WAIT_X, GUI_SCALE*BUTTON_WAIT_Y,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.BACK.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_BACK_X+8, GUI_SCALE*BUTTON_BACK_Y+8,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.BUTTON_BLUE_WIDE.getTexture();
		
		guiBatch.draw(texture,
				GUI_SCALE*BUTTON_ENTER_X, GUI_SCALE*BUTTON_ENTER_Y+12f,
			   0, 0,
			   texture.getRegionWidth(), texture.getRegionHeight(),
			   2, 2,
			   0);
		
		texture = Assets.GameSprite.REGISTER_BORDER.getTexture();
		
		CommandComponent commandComponent = Mappers.commandComponent.get(player);
		
		for (int i = 0; i < commandComponent.getMaxCommmands();i++) {
			tempColor = guiBatch.getColor();
			
			if (commandComponent.getStep() >= i+1)
				guiBatch.setColor(greenTint);
			
			guiBatch.draw(texture,
					GUI_SCALE*((REGISTERS_ORIGIN_X+0.2f) + i*3), GUI_SCALE*REGISTERS_ORIGIN_Y,
				   0, 0,
				   texture.getRegionWidth(), texture.getRegionHeight(),
				   2, 2,
				   0);
			guiBatch.setShader(Assets.fontShader);
			Assets.akashiFontSmall.draw(guiBatch, ""+(i+1), GUI_SCALE*((REGISTERS_ORIGIN_X+1) + i*3), GUI_SCALE*(REGISTERS_ORIGIN_Y+0.7f));
			guiBatch.setShader(null);
			guiBatch.setColor(tempColor);
		}
		
		if (commandComponent.getCommands() != null)
		{
			int i = 0;
			for (Command c : commandComponent.getCommands()) {
				
				if (MoveCommand.class.isAssignableFrom(c.getClass())) {
					Direction dir = ((MoveCommand)c).getDirection();
					switch (dir) {
					case EAST:
						texture = Assets.GameSprite.ARROW_EAST.getTexture();
						break;
					case NORTH:
						texture = Assets.GameSprite.ARROW_NORTH.getTexture();
						break;
					case SOUTH:
						texture = Assets.GameSprite.ARROW_SOUTH.getTexture();
						break;
					case WEST:
						texture = Assets.GameSprite.ARROW_WEST.getTexture();
						break;
					default:
						break;
					
					}
				}
				
				if (DoNothingCommand.class.isAssignableFrom(c.getClass())) {
					texture = Assets.GameSprite.CLOCK.getTexture();

				}
				
				guiBatch.draw(texture,
						GUI_SCALE*((REGISTERS_ORIGIN_X+0.2f) + i*3), GUI_SCALE*REGISTERS_ORIGIN_Y,
					   0, 0,
					   texture.getRegionWidth(), texture.getRegionHeight(),
					   2, 2,
					   0);
				i++;
			}
		}
		
		ImmutableArray<Entity> competitorEntities = engine.getSystem(CompetitorSystem.class).getCompetitiors();
		
		for (int e = 0; e < competitorEntities.size(); e++) {
			Entity entity = competitorEntities.get(e);
			
			// HEALTH DISPLAY
			texture = Mappers.textureComponent.get(entity).region;
			
			guiBatch.draw(texture,
					GUI_SCALE*PLAYER1_ORIGIN_X*(e*7.7f+1), GUI_SCALE*PLAYER1_ORIGIN_Y,
				   0, 0,
				   texture.getRegionWidth(), texture.getRegionHeight(),
				   2, 2,
				   0);
			
			texture = Assets.GameSprite.HEART.getTexture();
			
			guiBatch.draw(texture,
					GUI_SCALE*PLAYER1_ORIGIN_X*(e*7.7f+1.8f), GUI_SCALE*PLAYER1_ORIGIN_Y,
				   0, 0,
				   texture.getRegionWidth(), texture.getRegionHeight(),
				   2, 2,
				   0);
			
			texture = Assets.GameSprite.FINISH_FLAG.getTexture();
			
			guiBatch.draw(texture,
					GUI_SCALE*PLAYER1_ORIGIN_X*(e*7.7f+3.6f), GUI_SCALE*PLAYER1_ORIGIN_Y,
				   0, 0,
				   texture.getRegionWidth(), texture.getRegionHeight(),
				   2, 2,
				   0);
			
			texture = Assets.GameSprite.EXCLAMATION.getTexture();
			
			guiBatch.draw(texture,
					GUI_SCALE*PLAYER1_ORIGIN_X*(e*7.7f+5.2f), GUI_SCALE*PLAYER1_ORIGIN_Y,
				   0, 0,
				   texture.getRegionWidth(), texture.getRegionHeight(),
				   2, 2,
				   0);
			
			guiBatch.setShader(Assets.fontShader);
			
			tempColor = guiBatch.getColor();
			
			if (Mappers.statsComponent.get(entity).getCurrentHealth() <= 0)
				guiBatch.setColor(redTint);
				
			Assets.akashiFontSmall.draw(guiBatch, Mappers.statsComponent.get(entity).getCurrentHealth()+"", GUI_SCALE*(((PLAYER1_ORIGIN_X*(e*7.7f+1.5f))+1.0f)), GUI_SCALE*(PLAYER1_ORIGIN_Y+1.1f));

			Assets.akashiFontSmall.draw(guiBatch, (Mappers.competitorComponent.get(entity).place > 0)?Mappers.competitorComponent.get(entity).place+"":"0", GUI_SCALE*(((PLAYER1_ORIGIN_X*(e*7.7f+3.6f))+1.0f)), GUI_SCALE*(PLAYER1_ORIGIN_Y+1.1f));

			Assets.akashiFontSmall.draw(guiBatch, Mappers.competitorComponent.get(entity).score +"", GUI_SCALE*(((PLAYER1_ORIGIN_X*(e*7.7f+5.0f))+1.0f)), GUI_SCALE*(PLAYER1_ORIGIN_Y+1.1f));

			guiBatch.setColor(tempColor);
			
			guiBatch.setShader(null);
		}

		
		guiBatch.setShader(Assets.fontShader);
		
		Assets.akashiFontSmall.draw(guiBatch, "Finish Line: " + activeZoneComponent.zone.getMap().getTile( MathUtils.floor(Mappers.transformComponent.get(player).newPos.x),  MathUtils.floor(Mappers.transformComponent.get(player).newPos.y), Map.PATHING_LAYER) , GUI_SCALE*FINISH_ORIGIN_X, GUI_SCALE*FINISH_ORIGIN_Y);
		
		Array<String> terminal = engine.getSystem(TerminalSystem.class).getMessages();
		
		if (terminal != null && terminal.size > 0) {

			if (messageSize != terminal.size) {
				messageSize = terminal.size;
				displayRow.set(GUI_SCALE,MESSAGE_DISPLAY,0);
				Timeline.createSequence().push(Tween.to(displayRow, VectorAccessor.POS_XY, 0.5f)
					.target(toprow.x,toprow.y).ease(TweenEquations.easeOutQuint)
					.start(RobotArena.getTweenManager()));
			}
			
			float row = displayRow.y;
			for (int i = Math.max(0, terminal.size-MESSAGE_ROWS); i < terminal.size;i++) {
				if (terminal.get(i)!=null) {
					Assets.akashiFontSmall.draw(guiBatch, terminal.get(i), GUI_SCALE + 2, GUI_SCALE*row);
					row = row - 1;
				}
			}
		}		
		
		Assets.akashiFontSmall.draw(guiBatch, "Enter", GUI_SCALE*BUTTON_ENTER_TEXT_X, GUI_SCALE*BUTTON_ENTER_TEXT_Y+3);
		
		// TODO: REMOVE THIS
		//Assets.akashiFontSmall.draw(guiBatch, Mappers.transformComponent.get(player).newPos.toString() , GUI_SCALE*((REGISTERS_ORIGIN_X+20.0f)), GUI_SCALE*REGISTERS_ORIGIN_Y);
		//Assets.akashiFontSmall.draw(guiBatch, "" + activeZoneComponent.zone.getMap().getTile( MathUtils.floor(Mappers.transformComponent.get(player).newPos.x),  MathUtils.floor(Mappers.transformComponent.get(player).newPos.y), 0) , GUI_SCALE*((REGISTERS_ORIGIN_X+30.0f)), GUI_SCALE*REGISTERS_ORIGIN_Y);
		
		//Assets.arialFont.setScale(0.75f);
		//Assets.arialFont.draw(guiBatch, "Hello World", 24f, 24*5f);
		//Assets.fontSmall.draw(guiBatch, "Hello World", 24f, 24*7f);
		//Assets.akashiFont.draw(guiBatch, "Hello World", 24f, 24*9f);
		//Assets.arialFont.setScale(1f);
		guiBatch.setShader(null);
		
		guiBatch.end();
		//guiViewport.update(screenWidth, screenHeight * screenHeight/screenWidth);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		
		renderQueue.add(entity);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		activeZone = engine.getEntitiesFor(Family.getFor(ZoneComponent.class, ActiveZoneComponent.class));

	}
	
	public OrthographicCamera getGameCamera() {
		return gameCamera;
	}
	
	public boolean isPointInGameViewport(Vector3 point) {
		
		Vector3 mapCoord = point.cpy();
		getGameCameraUnproject(mapCoord);
		return mapArea.contains(mapCoord.x,mapCoord.y);
		//float x = point.x, y = point.y;
		//x = x - gameViewport.getScreenX();
		//y = Gdx.graphics.getHeight() - y - 1;
		
		//gameViewportBounds = new Rectangle(gameViewport.getScreenX(),gameViewport.getScreenHeight() * 0.25f , gameViewport.getScreenWidth(), gameViewport.getScreenHeight() * 0.75f);
		
		//return gameViewportBounds.contains(x,y);
	}
	
	public boolean isPointInGameMapViewport(Point point) {

		return mapArea.contains(point.x(),point.y());
		//float x = point.x, y = point.y;
		//x = x - gameViewport.getScreenX();
		//y = Gdx.graphics.getHeight() - y - 1;
		
		//gameViewportBounds = new Rectangle(gameViewport.getScreenX(),gameViewport.getScreenHeight() * 0.25f , gameViewport.getScreenWidth(), gameViewport.getScreenHeight() * 0.75f);
		
		//return gameViewportBounds.contains(x,y);
	}
	
	public boolean isPointInGuiViewport(Vector3 point) {
		
		Vector3 mapCoord = point.cpy();
		getGuiCameraUnproject(mapCoord);
		return guiArea.contains(mapCoord.x,mapCoord.y);
		//float x = point.x, y = point.y;
		//x = x - gameViewport.getScreenX();
		//y = Gdx.graphics.getHeight() - y - 1;
		
		//gameViewportBounds = new Rectangle(gameViewport.getScreenX(),gameViewport.getScreenHeight() * 0.25f , gameViewport.getScreenWidth(), gameViewport.getScreenHeight() * 0.75f);
		
		//return gameViewportBounds.contains(x,y);
	}
	
	public Vector3 getGameCameraUnproject(Vector3 point) {
		return gameCamera.unproject(point,gameViewport.getScreenX(),gameViewport.getScreenY(),gameViewport.getScreenWidth(),gameViewport.getScreenHeight()).add(OFFSETX, -OFFSETY, 0);
	}
	
	public Vector3 getGuiCameraUnproject(Vector3 point) {
		return guiCamera.unproject(point,guiViewport.getScreenX(),guiViewport.getScreenY(),guiViewport.getScreenWidth(),guiViewport.getScreenHeight());
	}
	
	
	
	public void updateSize(int width, int height) {
		if (gameViewport != null && guiViewport != null) {
			
			
			screenWidth = width;
		    screenHeight = height;
		    
			gameViewport.update(screenWidth, screenHeight);
			guiViewport.update(screenWidth, screenHeight, true);

		    gameViewportBounds = new Rectangle(gameViewport.getScreenX(),gameViewport.getScreenHeight() * 0.25f , gameViewport.getScreenWidth(), gameViewport.getScreenHeight() * 0.75f);
		    guiViewportBounds = new Rectangle(guiViewport.getScreenX(),guiViewport.getScreenY(), guiViewport.getScreenWidth(), guiViewport.getScreenHeight());
			
			//gameViewport.setScreenY(gameViewport.getScreenHeight());
			
			//Gdx.app.log("updateSize", "gameViewport: (" + gameViewport.getScreenX() + ", " + gameViewport.getScreenY() + ") width:" + gameViewport.getScreenWidth() + " height:" + gameViewport.getScreenHeight());
			//Gdx.app.log("updateSize", "guiViewport: (" + guiViewport.getScreenX() + ", " + guiViewport.getScreenY() + ") width:" + guiViewport.getScreenWidth() + " height:" + guiViewport.getScreenHeight());
		}		
	}

	public void dispose() {
/*		if (gameBatch != null && guiBatch != null) {
			gameBatch.dispose();
			guiBatch.dispose();
		}*/
		
	}

}
