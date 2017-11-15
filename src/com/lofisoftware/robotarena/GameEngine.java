package com.lofisoftware.robotarena;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.lofisoftware.robotarena.Assets.GameSprite;
import com.lofisoftware.robotarena.ai.EnemyBrain;
import com.lofisoftware.robotarena.ai.OpponentBrain;
import com.lofisoftware.robotarena.commands.DoNothingCommand;
import com.lofisoftware.robotarena.commands.LaserCommand;
import com.lofisoftware.robotarena.commands.PushCommand;
import com.lofisoftware.robotarena.commands.RepairCommand;
import com.lofisoftware.robotarena.commands.SmasherCommand;
import com.lofisoftware.robotarena.commands.SpikeCommand;
import com.lofisoftware.robotarena.components.ActiveZoneComponent;
import com.lofisoftware.robotarena.components.BoundsComponent;
import com.lofisoftware.robotarena.components.CameraComponent;
import com.lofisoftware.robotarena.components.CharacterComponent;
import com.lofisoftware.robotarena.components.CommandComponent;
import com.lofisoftware.robotarena.components.CompetitorComponent;
import com.lofisoftware.robotarena.components.EnemyBrainComponent;
import com.lofisoftware.robotarena.components.EnemyComponent;
import com.lofisoftware.robotarena.components.FieldOfViewComponent;
import com.lofisoftware.robotarena.components.HazardComponent;
import com.lofisoftware.robotarena.components.HeroComponent;
import com.lofisoftware.robotarena.components.InputComponent;
import com.lofisoftware.robotarena.components.ItemComponent;
import com.lofisoftware.robotarena.components.ItemComponent.ITEM_TYPE;
import com.lofisoftware.robotarena.components.MeleeWeaponComponent;
import com.lofisoftware.robotarena.components.OpponentBrainComponent;
import com.lofisoftware.robotarena.components.StateComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.StatsComponent;
import com.lofisoftware.robotarena.components.TextureComponent;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.ZoneChangeMessage;
import com.lofisoftware.robotarena.systems.HazardSystem;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.systems.PlayerInputSystem;
import com.lofisoftware.robotarena.systems.RenderingSystem;
import com.lofisoftware.robotarena.systems.TurnSystem;
import com.lofisoftware.robotarena.util.Line;
import com.lofisoftware.robotarena.util.MazeGenerator;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;
import com.lofisoftware.robotarena.world.Map;
import com.lofisoftware.robotarena.world.Tile;
import com.lofisoftware.robotarena.world.Zone;
import com.lofisoftware.robotarena.world.ZoneBuilder;

public class GameEngine implements Telegraph {
	
	private Entity activeZone;
	private Entity hero;
	
	public Long seed = 10002L;
	public Random rand;
	
	private PooledEngine engine;
	private int playerNumber;
	
	private static float CHANCE_LASER = 0.9f;
	private static float CHANCE_CONVEYOR = 0.9f;
	private static float CHANCE_SMASHER = 0.9f;
	private static float CHANCE_SPIKE = 0.9f;
	private static float CHANCE_REPAIR = 0.9f;
	private static final int MOBS_IN_ROOM = 4;
	
	public GameEngine (PooledEngine engine) {
		this.engine = engine;
		rand = new Random();
		playerNumber = 2;
			
	}

	public PooledEngine getEngine() {
		return engine;
	}

	public void create() {
		
		activeZone = createZones();
		//long zoneID = activeZone.getId();
		
		Rectangle startArea = Mappers.zoneComponent.get(activeZone).zone.getStartingArea();
		Rectangle finishArea = Mappers.zoneComponent.get(activeZone).zone.getFinishingArea();
		
		Vector2 center = new Vector2(0,0);
		startArea.getCenter(center);
		
		Point position = new Point (center.x,center.y);
		
		List<Point> startingLine = Arrays.asList(
				new Point(position.x()-2,position.y()), position, new Point(position.x()+2,position.y()), new Point(position.x()+4,position.y()));

		Collections.shuffle(startingLine);

		hero = createHero(GameSprite.PLAYER1,activeZone, startingLine.get(0).x(),startingLine.get(0).y());
		
		createOpponent("Larry", GameSprite.PLAYER2, activeZone, startingLine.get(1).x(),startingLine.get(0).y());
		
		createOpponent("Curly", GameSprite.PLAYER3, activeZone, startingLine.get(2).x(),startingLine.get(0).y());
		
		createOpponent("Moe", GameSprite.PLAYER4, activeZone, startingLine.get(3).x(),startingLine.get(0).y());
		
		fillRooms(activeZone);
		
		
		finishArea.getCenter(center);
		Mappers.zoneComponent.get(activeZone).zone.calculatePaths( MathUtils.floor(center.x),MathUtils.floor(center.y));
		//Mappers.zoneComponent.get(activeZone).zone.printMap(Map.PATHING_LAYER);
		
		//createEnemy(GameSprite.BROWN_ROBOT,hero, activeZone, 5,5);
		
		//createEnemy(GameSprite.BROWN_ROBOT,hero, activeZone, 5,3);
		
		//createEnemy(GameSprite.BROWN_ROBOT,hero, activeZone, 5,7);
		
		//createEnemy("Larry", GameSprite.BROWN_ROBOT,hero, activeZone, MathUtils.floor(startArea.x+2),MathUtils.floor(startArea.y+4));
		
		//createEnemy("Curly", GameSprite.BLUE_ROBOT,hero, activeZone, MathUtils.floor(startArea.x+2),MathUtils.floor(startArea.y+6));
		
		//createEnemy("Moe", GameSprite.GREEN_ROBOT,hero, activeZone, MathUtils.floor(startArea.x+2),MathUtils.floor(startArea.y+8));
		
		//createLaser(Direction.NORTH, activeZone, MathUtils.floor(startArea.x+4),MathUtils.floor(startArea.y+2));
		
		//createLaser(Direction.SOUTH, activeZone, 8,6, true);
		
		//createLaser(Direction.EAST, activeZone, 10,2, true);
		
		//createLaser(Direction.WEST, activeZone, 12,12, true);
		//createConveyor(Direction.EAST, 6, activeZone, MathUtils.floor(startArea.x+5),MathUtils.floor(startArea.y+6));
		
		createCamera(hero);
	}
	
	public Tile getZoneTile(int x, int y, int z) {
		return Mappers.zoneComponent.get(activeZone).zone.getZoneTile(x,y,z);
	}
	
	public boolean isPassable(int x, int y) {

		return Mappers.zoneComponent.get(activeZone).zone.isPassable(x, y);
	}

	public Entity getActiveZone() {
		return activeZone;
	}

	public Entity getHero() {
		return hero;
	}

	private Entity createZones() {
		
		Zone zone1 = new ZoneBuilder(100, 100).buildDungeonZone(rand);
		Entity zoneEntity1 = createZoneEntity(zone1, true);
		
		//fillRooms(zoneEntity1);
		//Zone zone1 = new ZoneBuilder(30, 30).buildGridDungeon(3,3);
		//Zone zone2 = new ZoneBuilder(Zone.ZONE_DEFAULT_WIDTH, Zone.ZONE_DEFAULT_HEIGHT).buildRandomOverworldZone();
		//Zone zone3 = new ZoneBuilder(30, 15).buildDragStrip();
		//zone1.setZoneTile(5, 5, 0, Tile.DUNGEON_STAIR_UP);
		//zone2.setZoneTile(2, 2, 0, Tile.DUNGEON_STAIR_DOWN);
		//zone2.setZoneTile(4, 4, 0, Tile.DUNGEON_STAIR_UP);
		//zone3.setZoneTile(4, 4, 0, Tile.DUNGEON_STAIR_DOWN);
		
		
		
		//Entity zoneEntity1 = createZoneEntity(zone1, false);
		
		//Entity zoneEntity2 =  createZoneEntity(zone2, false);
		
		//Entity zoneEntity3 =  createZoneEntity(zone3, true);
		
		//engine.getSystem(ZoneSystem.class).createZoneConnection(zoneEntity1, new Point(5,5), zoneEntity2, new Point(2,2));
		//engine.getSystem(ZoneSystem.class).createZoneConnection(zoneEntity2, new Point(4,4), zoneEntity3, new Point(4,4));
		
		return zoneEntity1;
	}

	private void fillRooms(Entity zoneEntity) {

		Zone zone = Mappers.zoneComponent.get(zoneEntity).zone;
		Vector2 center = new Vector2(0,0);
		
		for (Entry<Rectangle> entry : zone.getRooms().entries()) {
			
			Rectangle room = entry.value; 
			Gdx.app.log("fillRooms", "Filling Room: " + room.toString());
			room.getCenter(center);
			Point position = getRandomValidPointInRoom(zone, room);
			
			Point point1 = new Point(0,0);
			Point point2 = new Point(0,0);
			
			
			if (room.overlaps(zone.getStartingArea())) {
				
				point1.set(room.x+1, center.y+1);
				point2.set(room.x+room.width-2, center.y+1);
				addTextLine(zone, point1, point2, Tile.FLOOR_BIG_CHECK, "START");
				point1.set(room.x+1, center.y);
				point2.set(room.x+room.width-2, center.y);
				addLine(zone, point1, point2, Tile.FLOOR_BIG_CHECK);
				point1.set(room.x+1, center.y-1);
				point2.set(room.x+room.width-2, center.y-1);
				addLine(zone, point1, point2, Tile.FLOOR_BIG_CHECK);
				
				//createMazeRoom(activeZone,room);
				
			}			
			else if (room.overlaps(zone.getFinishingArea())) {
				
				point1.set(room.x+1, center.y+1);
				point2.set(room.x+room.width-2, center.y+1);
				addTextLine(zone, point1, point2, Tile.FLOOR_SMALL_CHECK, "FINISH");
				point1.set(room.x+1, center.y);
				point2.set(room.x+room.width-2, center.y);
				addLine(zone, point1, point2, Tile.FLOOR_SMALL_CHECK);
				point1.set(room.x+1, center.y-1);
				point2.set(room.x+room.width-2, center.y-1);
				addLine(zone, point1, point2, Tile.FLOOR_SMALL_CHECK);
				
			}
			else {
				
				float chance = rand.nextFloat();
				
				if (chance < .25) 
					createFactoryRoom(activeZone,room);
				else if (chance < .5)
					createLaserChicaneRoom(activeZone,room);
				else if (chance < .7)
					createRandomRoom(activeZone,room);
				else if (chance < .9)
					createSpikeRoom(activeZone,room);
				else
					createEmptyRoom(activeZone,room);

			}
			
			/*if (rand.nextFloat() < CHANCE_LASER)
				createLaser(Direction.randomDirection(), activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);
			
			if (rand.nextFloat() < CHANCE_CONVEYOR)
				createConveyor(Direction.randomDirection(), 5, activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);

			if (rand.nextFloat() < CHANCE_SMASHER)
				createSmasher(Direction.randomDirection(), activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);

			if (rand.nextFloat() < CHANCE_SPIKE)
				createSpike(Direction.randomDirection(), activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);

			if (rand.nextFloat() < CHANCE_REPAIR)
				createRepairItem(activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);
			
			createEnemy("Blob", GameSprite.GREEN_BLOB, activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);
			
			createEnemy("Eye", GameSprite.EYEBALL, activeZone, position.x(),position.y());
			
			position = getRandomValidPointInRoom(zone, room);
			
			createEnemy("Thing", GameSprite.GREEN_CHECKED_MOB, activeZone, position.x(),position.y());*/
			
		}
		
	}

	private void createEmptyRoom(Entity activeZone2, Rectangle room) {

		if (rand.nextFloat() < 0.1f) {
			Vector2 center = new Vector2(0,0);
			room.getCenter(center);
			
			Point point1 = new Point(0,0);
			Point point2 = new Point(0,0);
			
			point1.set(room.x+2, center.y);
			point2.set(room.x+room.width-3, center.y);
			
			String message = "";
			
			switch (rand.nextInt(4)) {
			case 1:
				message = "Help me";
				break;
			case 2:
				message = "Go!";
				break;
			case 3:
				message = "Red was here";
				break;
			case 4:
				message = "Turn back";
				break;
			}
			addTextLine(Mappers.zoneComponent.get(activeZone).zone, point1, point2, Tile.DUNGEON_FLOOR, message);
		}
		
		addRandomFeature(Mappers.zoneComponent.get(activeZone).zone, room, Tile.SMALL_SKULL, Tile.DUNGEON_FLOOR, rand, 0.02f);
		addEnemiesToRoom(activeZone, room, "Thing", GameSprite.GREEN_CHECKED_MOB, MOBS_IN_ROOM);
		
		if (rand.nextFloat() < CHANCE_REPAIR-0.2f) {
			Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
			createRepairItem(activeZone, position.x(),position.y());
		}
	}

	private void createFactoryRoom(Entity zone, Rectangle room) {
		Vector2 center = new Vector2(0,0);
		room.getCenter(center);
		
		if (rand.nextFloat() < 0.7f) {
			createConveyorLoop(activeZone,new Rectangle(room.x +2, room.y +2, room.width - 4, room.height - 4), rand.nextBoolean());
		
			if (rand.nextFloat() < 0.8f && room.width > 16 && room.height > 16 ){
				createConveyorLoop(activeZone,new Rectangle(room.x +4, room.y +4, room.width - 8, room.height - 8), rand.nextBoolean());
				
				if (rand.nextFloat() < 0.7f && room.width > 21 && room.height > 21 ){
					createConveyorLoop(activeZone,new Rectangle(room.x +6, room.y +6, room.width - 12, room.height - 12), rand.nextBoolean());
				}
			}
		}
		else {
			Direction direction = Direction.randomDirection();
			
			Point position = new Point(0,0);
			int length = 0;
			
			switch (direction) {
			case EAST:
				position.set(room.x+4, room.y+room.height/3);
				length = (int) (room.width-5);
				createConveyor(direction, length, zone, position.x(),position.y());
				createFire(zone,MathUtils.floor(position.x()+length-1),position.y());
				
				position.set(room.x+room.width-4, room.y+(2*room.height)/3);
				length = (int) (room.width-5);
				createConveyor(Direction.WEST, length, zone, position.x(),position.y());
				createFire(zone,MathUtils.floor(position.x()-length),position.y());
				
				break;
			case NORTH:
				position.set(room.x+room.width/3, room.y+4);
				length = (int) (room.height-5);
				createConveyor(direction, length, zone, position.x(),position.y());
				createFire(zone,position.x(), MathUtils.floor(position.y()+length-1));
				
				position.set(room.x+(2*room.width)/3, room.y+room.height -4);
				length = (int) (room.height-5);
				createConveyor(Direction.SOUTH, length, zone, position.x(),position.y());
				createFire(zone,position.x(), MathUtils.floor(position.y()-length));
				break;
			case SOUTH:
				position.set(room.x+room.width/3, room.y+room.height -4);
				length = (int) (room.height-5);
				createConveyor(direction, length, zone, position.x(),position.y());
				createFire(zone,position.x(), MathUtils.floor(position.y()-length));
				
				position.set(room.x+(2*room.width)/3, room.y+4);
				length = (int) (room.height-5);
				createConveyor(Direction.NORTH, length, zone, position.x(),position.y());
				createFire(zone,position.x(), MathUtils.floor(position.y()+length-1));
				break;
			case WEST:
				position.set(room.x+room.width-4, room.y+room.height/3);
				length = (int) (room.width-5);
				createConveyor(direction, length, zone, position.x(),position.y());
				createFire(zone,MathUtils.floor(position.x()-length),position.y());
				
				position.set(room.x+4, room.y+(2*room.height)/3);
				length = (int) (room.width-5);
				createConveyor(Direction.EAST, length, zone, position.x(),position.y());
				createFire(zone,MathUtils.floor(position.x()+length-1),position.y());
				break;
			default:
				break;
			
			}

		}
		
		addRandomFeature(Mappers.zoneComponent.get(activeZone).zone, room, Tile.TERMINAL2, Tile.DUNGEON_FLOOR, rand, 0.02f);
		addEnemiesToRoom(activeZone, room, "Drone", rand.nextBoolean()?GameSprite.WORKER_BLUE:GameSprite.WORKER_RED, MOBS_IN_ROOM);
		
		if (rand.nextFloat() < CHANCE_REPAIR+0.2f) {
			Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
			createRepairItem(activeZone, position.x(),position.y());
		}
	}

	private void createConveyorLoop(Entity zone, Rectangle loop, boolean clockwise) {
		
		if (clockwise) {
			createConveyor(Direction.NORTH, loop.height-1, zone, loop.x,loop.y);
			createConveyor(Direction.EAST, loop.width-1, zone, loop.x,loop.y + loop.height-1);
			createConveyor(Direction.SOUTH, loop.height-1, zone, loop.x+loop.width-1,loop.y + loop.height-1);
			createConveyor(Direction.WEST, loop.width-1, zone, loop.x+loop.width-1,loop.y);

		}
		else {
			createConveyor(Direction.SOUTH, loop.height-1, zone, loop.x,loop.y+ loop.height-1);
			createConveyor(Direction.WEST, loop.width-1, zone, loop.x+loop.width-1,loop.y + loop.height-1);
			createConveyor(Direction.NORTH, loop.height-1, zone, loop.x+loop.width-1,loop.y);
			createConveyor(Direction.EAST, loop.width-1, zone, loop.x,loop.y);
		}
	}
	
	private void createConveyor(Direction direction, float length, Entity activeZone2,
			float x, float y) {
		createConveyor(direction, MathUtils.floor(length), activeZone2,MathUtils.floor(x), MathUtils.floor(y));
		
	}

	private void addEnemiesToRoom(Entity zone, Rectangle room, String name, GameSprite sprite, int count) {
		
		Point position = null;
		for (int e = 0; e < count; e++) {
			position = getRandomValidPointInRoom(Mappers.zoneComponent.get(zone).zone, room);
			if (position != null)
				createEnemy(name, sprite, zone, position.x(), position.y());
		}
	}
	
	private void createLaserChicaneRoom(Entity zone, Rectangle room) {
		Vector2 center = new Vector2(0,0);
		room.getCenter(center);
		
		if (rand.nextFloat() < 0.3f) {
			
			Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
			
			createLaser(Direction.randomDirection(), activeZone, position.x(),position.y());
			
		
			if (rand.nextFloat() < 0.8f && room.width > 14 && room.height > 14 ){
				position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
				
				createLaser(Direction.randomDirection(), activeZone, position.x(),position.y());
				
				if (rand.nextFloat() < 0.7f && room.width > 20 && room.height > 20 ){
					position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
					
					createLaser(Direction.randomDirection(), activeZone, position.x(),position.y());
				}
			}
		}
		else {
			Direction direction = Direction.randomDirection();
			
			Point position = new Point(0,0);
			int length = 0;
			
			switch (direction) {
			case EAST:
				position.set(room.x+4, room.y+room.height/3);
				length = (int) (room.width-5);
				createLaser(direction, zone, position.x(),position.y());
				//createFire(zone,MathUtils.floor(position.x()+length-1),position.y());
				
				position.set(room.x+room.width-4, room.y+(2*room.height)/3);
				length = (int) (room.width-5);
				createLaser(Direction.WEST, zone, position.x(),position.y());
				//createFire(zone,MathUtils.floor(position.x()-length),position.y());
				
				break;
			case NORTH:
				position.set(room.x+room.width/3, room.y+4);
				length = (int) (room.height-5);
				createLaser(direction, zone, position.x(),position.y());
				//createFire(zone,position.x(), MathUtils.floor(position.y()+length-1));
				
				position.set(room.x+(2*room.width)/3, room.y+room.height -4);
				length = (int) (room.height-5);
				createLaser(Direction.SOUTH, zone, position.x(),position.y());
				//createFire(zone,position.x(), MathUtils.floor(position.y()-length));
				break;
			case SOUTH:
				position.set(room.x+room.width/3, room.y+room.height -4);
				length = (int) (room.height-5);
				createLaser(direction, zone, position.x(),position.y());
				//createFire(zone,position.x(), MathUtils.floor(position.y()-length));
				
				position.set(room.x+(2*room.width)/3, room.y+4);
				length = (int) (room.height-5);
				createLaser(Direction.NORTH, zone, position.x(),position.y());
				//createFire(zone,position.x(), MathUtils.floor(position.y()+length-1));
				break;
			case WEST:
				position.set(room.x+room.width-4, room.y+room.height/3);
				length = (int) (room.width-5);
				createLaser(direction, zone, position.x(),position.y());
				//createFire(zone,MathUtils.floor(position.x()-length),position.y());
				
				position.set(room.x+4, room.y+(2*room.height)/3);
				length = (int) (room.width-5);
				createLaser(Direction.EAST, zone, position.x(),position.y());
				//createFire(zone,MathUtils.floor(position.x()+length-1),position.y());
				break;
			default:
				break;
			
			}

		}
		
		addRandomFeature(Mappers.zoneComponent.get(activeZone).zone, room, Tile.GREEN_CPU, Tile.DUNGEON_FLOOR, rand, 0.02f);
		addEnemiesToRoom(activeZone, room, "Watcher", GameSprite.EYEBALL, MOBS_IN_ROOM);
		
		if (rand.nextFloat() < CHANCE_REPAIR) {
			Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
			createRepairItem(activeZone, position.x(),position.y());
		}
	}

	private void createRandomRoom(Entity zone, Rectangle room) {

		Vector2 center = new Vector2(0,0);
		room.getCenter(center);
		
		Rectangle conveyorBelt = new Rectangle(room.x +4, room.y +4, room.width - 8, room.height - 8);
		
		if (rand.nextFloat() < 0.6f)
			createConveyorLoop(activeZone, conveyorBelt, rand.nextBoolean());
		
		if (rand.nextFloat() < 0.6f)
			createLaser(Direction.randomDirection(), zone, MathUtils.floor(center.x), MathUtils.floor(center.y));
		
		if (rand.nextFloat() < 0.6f)
			createSmasherLoop(activeZone,conveyorBelt, 0.3f);
		else if (rand.nextFloat() < 0.3f)
			createSpikeLoop(activeZone, conveyorBelt, true);
		
		if (rand.nextFloat() < 0.6f)
			createSpikeLoop(activeZone, room, false);
		
		if (rand.nextFloat() < CHANCE_REPAIR) {
			Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
			createRepairItem(activeZone, position.x(),position.y());
		}
		
		addRandomFeature(Mappers.zoneComponent.get(activeZone).zone, room, Tile.GREEN_CPU, Tile.DUNGEON_FLOOR, rand, 0.02f);
		
		if (rand.nextFloat() < 0.4f)
			addEnemiesToRoom(activeZone, room, "Drone", GameSprite.WORKER_WHITE, MOBS_IN_ROOM);
		else if (rand.nextFloat() < 0.4f)
			addEnemiesToRoom(activeZone, room, "Drone", GameSprite.WORKER_YELLOW, MOBS_IN_ROOM);
		else if (rand.nextFloat() < 0.4f)
			addEnemiesToRoom(activeZone, room, "Ember", GameSprite.GLOWING_BLOB, MOBS_IN_ROOM);
		
	}

	private void createSmasherLoop(Entity zone, Rectangle loop, float chance) {
		
		for(int x = (int) (loop.x+1);x < loop.x+loop.width-1;x++) {
			for(int y = (int) loop.y+1;y < loop.y+loop.height-1;y++) {
				
				if (isDoor(Mappers.zoneComponent.get(zone).zone, x, y))
					continue;
				
				if (x == loop.x+1 && rand.nextFloat() < chance)
					createSmasher(Direction.WEST, zone, x, y);
				else if (x == loop.x+loop.width-2 && rand.nextFloat() < chance)
					createSmasher(Direction.EAST, zone, x, y);
				else if (y == loop.y+1 && rand.nextFloat() < chance)
					createSmasher(Direction.SOUTH, zone, x, y);
				else if (y == loop.y+loop.height-2 && rand.nextFloat() < chance)
					createSmasher(Direction.NORTH, zone, x, y);
				
			}
		}
		
	}
	
	private void createSpikeLoop(Entity zone, Rectangle loop, boolean inner) {
		
		for(int x = (int) (loop.x+1);x < loop.x+loop.width-1;x++) {
			for(int y = (int) loop.y+1;y < loop.y+loop.height-1;y++) {
				
				if (isDoor(Mappers.zoneComponent.get(zone).zone, x, y))
					continue;
				
				if (inner) {
					if (x == loop.x+1)
						createSpike(Direction.WEST, zone, x, y);
					else if (x == loop.x+loop.width-2)
						createSpike(Direction.EAST, zone, x, y);
					else if (y == loop.y+1)
						createSpike(Direction.SOUTH, zone, x, y);
					else if (y == loop.y+loop.height-2)
						createSpike(Direction.NORTH, zone, x, y);
				}
				else {
					if (x == loop.x+1)
						createSpike(Direction.EAST, zone, x, y);
					else if (x == loop.x+loop.width-2)
						createSpike(Direction.WEST, zone, x, y);
					else if (y == loop.y+1)
						createSpike(Direction.NORTH, zone, x, y);
					else if (y == loop.y+loop.height-2)
						createSpike(Direction.SOUTH, zone, x, y);
				}
			}
		}
		
	}

	private void createSpikeRoom(Entity zone, Rectangle room) {

		if (rand.nextFloat() < 0.5f) {
			for(int x = (int) (room.x+1);x < room.x+room.width-1;x++) {
				for(int y = (int) room.y+1;y < room.y+room.height-1;y++) {
					
					if (isDoor(Mappers.zoneComponent.get(zone).zone, x, y))
						continue;
					
					if (x == room.x+1)
						createSpike(Direction.EAST, zone, x, y);
					else if (x == room.x+room.width-2)
						createSpike(Direction.WEST, zone, x, y);
					else if (y == room.y+1)
						createSpike(Direction.NORTH, zone, x, y);
					else if (y == room.y+room.height-2)
						createSpike(Direction.SOUTH, zone, x, y);
					
				}
			}
		}
		else {
			createSpikeClusters(zone,room,3);
		}
		
		addRandomFeature(Mappers.zoneComponent.get(activeZone).zone, room, Tile.BLOOD, Tile.DUNGEON_FLOOR, rand, 0.02f);
		addEnemiesToRoom(activeZone, room, "Blob", GameSprite.GREEN_BLOB, MOBS_IN_ROOM);
		
		if (rand.nextFloat() < CHANCE_REPAIR) {
			Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, room);
			createRepairItem(activeZone, position.x(),position.y());
		}
		
	}

	private void createSpikeClusters(Entity zone, Rectangle room, int count) {

		Rectangle spikeArea = new Rectangle(room.x +4, room.y +4, room.width - 8, room.height - 8);
		
		Point position = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, spikeArea);
		
		int retries = 10;
		
		for (int s = 0; s < count;s++) {
			if (position != null) {
				Mappers.zoneComponent.get(activeZone).zone.setZoneTile(position.x(), position.y(), Zone.ZONE_LAYER.BASE.getLayer(), Tile.DUNGEON_WALL);
	
				createSpike(Direction.EAST, zone, position.x()+1, position.y());
	
				createSpike(Direction.WEST, zone, position.x()-1, position.y());
	
				createSpike(Direction.NORTH, zone, position.x(), position.y()+1);
	
				createSpike(Direction.SOUTH, zone, position.x(), position.y()-1);
			}
			
			while (retries-- > 0) {
				Point newPosition = getRandomValidPointInRoom(Mappers.zoneComponent.get(activeZone).zone, spikeArea);
				
				if (position.distanceTo(newPosition) > 4) {
					position = newPosition.copy();
					break;
				}
				if (retries <= 1)
					return;
			}
		}
			
		
	}

	public void createMazeRoom(Entity zone, Rectangle room) {

		
		
	}
	
	public void addTextLine(Zone zone, Point one, Point two, Tile tile, String text) {
		
		addTextLine(zone, addLine(zone, one, two, tile), text);
		
	}
	
	public void addTextLine(Zone zone, Line line, String text) {
		
		if (line.getPoints().size() >= text.length()) {
			int pointNumber = 0;
			int characterNumber = 0;
			int spacing = MathUtils.round((float)line.getPoints().size() / text.length());
			//Gdx.app.log("ZoneBuilder:addTextLine", "spacing:" + spacing);
			
			for (Point p : line) {
				if (pointNumber%spacing == 0 && characterNumber < text.length()) {
					zone.setZoneTile(p.x(), p.y(), Zone.ZONE_LAYER.FEATURE.getLayer(), Tile.getTileByName(text.substring(characterNumber, ++characterNumber)));
				}
				pointNumber++;
			}
			
		}
		else
			Gdx.app.error("ZoneBuilder:addTextLine", "Line not long enough for Text");
	}
	
	public Line addLine(Zone zone, Point one, Point two, Tile tile) {
		
		Line line = new Line(one.x(),one.y(),two.x(),two.y());
		
		for (Point p : line) {
				zone.setZoneTile(p.x(), p.y(), Zone.ZONE_LAYER.BASE.getLayer(), tile);
		}
		
		return line;
		
	}
	
	private void addRandomFeature(Zone zone, Rectangle room, Tile feature, Tile baseTile, Random rand, float chance) {
		
		for (int x = (int) (room.x+2); x < room.x+room.width-4; x++) {
			for (int y = (int) (room.y + 2); y < room.y+room.height-4; y++) {
				
				if (zone.getZoneTile(x, y, Zone.ZONE_LAYER.BASE.getLayer()) == baseTile 
						&& rand.nextFloat() < chance && !isDoor(zone, x, y))
					zone.setZoneTile(x, y, Zone.ZONE_LAYER.FEATURE.getLayer(), feature);					
			}
		}
		//Gdx.app.log("ZoneBuilder: ", "addRandomFeature: " + feature.getName());
	}
	
	public Point getRandomValidPointInRoom(Zone zone, Rectangle room) {
		int maxTries = 50;
		
		while (maxTries-- >= 0) {
		Point p = getRandomPointInRect(room);
		
		if (zone.isPassable(p.x(), p.y()) && !isDoor(zone,p.x(), p.y()) )
			return p;
		}
		return new Point(0,0);
	}
	
	// TODO: Fix this to not be fixed on floor tile - DONE? now dependent on map
	public boolean isDoor(Zone zone, int x, int y) {
		//return zone.getZoneTile(x, y, Zone.ZONE_LAYER.BASE.getLayer()) == Tile.DUNGEON_DOOR_OPEN;
		
		if ( zone.getMap().getRoomByID(zone.getMap().getTile(x, y, 0)) != null &&
				zone.getMap().getRoomByID(zone.getMap().getTile(x, y, 0)).isDoor(x, y))
			return true;
		
		for (Point neighbor : new Point(x,y).neighbors4()){
			if ( zone.getMap().getRoomByID(zone.getMap().getTile(neighbor.x(), neighbor.y(), 0)) != null &&
					zone.getMap().getRoomByID(zone.getMap().getTile(neighbor.x(), neighbor.y(), 0)).isDoor(neighbor.x(), neighbor.y()))
				return true;
			
		}
		return false;
	}
	
	public boolean isEntityAt(Entity zone, int x, int y) {
		
		if (engine.getSystem(HazardSystem.class).getHazardsAt(x, y, zone.getId()) != null)
			return true;
		
		return false;
	}
	
	public Point getRandomPointInRect(Rectangle rect) {
		Point p = new Point(0,0);
		p.set(rand.nextInt(MathUtils.floor(rect.width))+rect.x,rand.nextInt(MathUtils.floor(rect.height))+rect.y);
		return p;
	}
	
	private Entity createZoneEntity(Zone zone, boolean active) {
		Entity zoneEntity = engine.createEntity();
		ZoneComponent zoneComponent = engine.createComponent(ZoneComponent.class);
		
		zoneComponent.zone = zone;
		zoneComponent.zoneID = zoneEntity.getId();
		zoneEntity.add(zoneComponent);
		
		if (active) {
			setActiveZone(zoneEntity);
		}
		
		engine.addEntity(zoneEntity);
		
		return zoneEntity;
	}
	
	public boolean switchZones() {
		
		return true;
	}
	
	public boolean setActiveZone(Entity entity) {
		
		ZoneComponent zoneComponent = Mappers.zoneComponent.get(entity);
		
		if(zoneComponent != null) { 
		
			ActiveZoneComponent activezoneComponent = engine.createComponent(ActiveZoneComponent.class);
			
			if (activeZone != null && zoneComponent != null) {
				Mappers.fieldOfViewComponent.get(hero).fieldOfView.get(Mappers.zoneComponent.get(activeZone).zoneID).resetVisible(false);
				activeZone.remove(ActiveZoneComponent.class);
			}
			
			entity.add(activezoneComponent);
			activeZone = entity;
			
			return true;	
		}
		return false;
	}
	
	private Entity createHero(GameSprite sprite, Entity activeZone2, int x, int y) {
		hero = createCharacter("You", sprite, activeZone2, x,y, 10);
		
		HeroComponent heroComponent = engine.createComponent(HeroComponent.class);
		//AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		CommandComponent commandComponent = engine.createComponent(CommandComponent.class);
		FieldOfViewComponent fov = engine.createComponent(FieldOfViewComponent.class);
		InputComponent input = engine.createComponent(InputComponent.class);
		CompetitorComponent competitor = new CompetitorComponent();

		hero.getComponent(StateComponent.class).set(STATE.READY);
		Mappers.turnComponent.get(hero).active = true;
		//animation.animations.put(hero.getComponent(StateComponent.class).getInt(), new Animation(0.75f,sprite.getAnimationTextures(),Animation.PlayMode.LOOP));
		
		fov.visibilityRadius = 7;
		competitor.player = 1;
		
		//hero.add(animation);
		hero.add(heroComponent);
		hero.add(competitor);
		hero.add(input);
		hero.add(commandComponent);
		hero.add(fov);
		
		engine.getSystem(PlayerInputSystem.class).setPlayer(hero);
		
		engine.addEntity(hero);
		
		return hero;
	}

	private Entity createOpponent(String name, GameSprite sprite, Entity activeZone2, int x, int y) {
		Entity opponent = createCharacter(name, sprite, activeZone2, x,y, 10);
		
		
		//AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		Mappers.turnComponent.get(opponent).active = true;
		opponent.getComponent(StateComponent.class).set(STATE.READY);
		//animation.animations.put(enemy.getComponent(StateComponent.class).getInt(), new Animation(0.75f,sprite.getAnimationTextures(),Animation.PlayMode.LOOP));
		
		//enemy.add(animation);
		FieldOfViewComponent fov = engine.createComponent(FieldOfViewComponent.class);
		fov.visibilityRadius = 7;
		opponent.add(fov);
		
		CompetitorComponent competitor = new CompetitorComponent();
		competitor.player = playerNumber++;
		opponent.add(competitor);
		
		OpponentBrainComponent opponentBrainComponent = engine.createComponent(OpponentBrainComponent.class);
		opponentBrainComponent.opponentBrain = new OpponentBrain(engine, opponent, activeZone2);
		opponent.add(opponentBrainComponent);
		
		engine.addEntity(opponent);
		
		return opponent;
	}
	
	private Entity createEnemy(String name, GameSprite sprite, Entity activeZone2, int x, int y) {
		Entity enemy = createCharacter(name, sprite, activeZone2, x,y, 1);
		
		enemy.add(new EnemyComponent());
		
		//AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		
		enemy.getComponent(StateComponent.class).set(STATE.READY);
		//animation.animations.put(enemy.getComponent(StateComponent.class).getInt(), new Animation(0.75f,sprite.getAnimationTextures(),Animation.PlayMode.LOOP));
		
		//enemy.add(animation);
		
		EnemyBrainComponent enemyBrainComponent = engine.createComponent(EnemyBrainComponent.class);
		enemyBrainComponent.enemyBrain = new EnemyBrain(engine, enemy, activeZone2);
		enemy.add(enemyBrainComponent);
		
		engine.addEntity(enemy);
		
		return enemy;
	}
	
	private Entity createCharacter(String name, GameSprite sprite, Entity activeZone2, int x, int y, int health) {
		Entity character = engine.createEntity();
		
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		//MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		MeleeWeaponComponent melee = engine.createComponent(MeleeWeaponComponent.class);
		StatsComponent stats = engine.createComponent(StatsComponent.class);
		CharacterComponent characterComponent = engine.createComponent(CharacterComponent.class);
		TurnComponent turnComponent = engine.createComponent(TurnComponent.class);
		//FieldOfViewComponent fov = engine.createComponent(FieldOfViewComponent.class);

		
		
		bounds.bounds.width = 1;
		bounds.bounds.height = 1;
		
		position.pos.set(x, y, 1.0f);
		position.newPos.set(x, y, 0.0f);
		position.zoneID = activeZone2.getId();
		
		texture.region = sprite.getTexture();
		
		melee.setWeaponType(MeleeWeaponComponent.MELEE_WEAPON.SPIKE);
		melee.setDamageMin(1);
		melee.setDamageMax(1);
		
		stats.setMaxHealth(health);
		stats.setCurrentHealth(health);
		stats.setMaxMana(10);
		stats.setCurrentMana(10);
		stats.setStrength(1);
		
		characterComponent.setName(name);
		
		turnComponent.priority = engine.getSystem(TurnSystem.class).getNextTurnNumber();
		turnComponent.active = false;
		
		//fov.visibilityRadius = 7;
		
		character.add(bounds);
		//character.add(movement); //FIXME - should not need?
		character.add(position);
		character.add(state);
		character.add(texture);
		character.add(melee);
		character.add(stats);
		character.add(characterComponent);
		character.add(turnComponent);
		//character.add(fov);

		
		//Gdx.app.log("createCharacter:"," character ID: " + character.getId());
		
		return character;
	}
	
	private Entity createLaser(Direction direction,	Entity activeZone, int x, int y) {
		
		GameSprite turretSprite, laserSprite;
		
		switch (direction) {
		case EAST:
			turretSprite = GameSprite.LASER_EAST;
			laserSprite = GameSprite.LASER_BEAM_EW;
			break;
		case NORTH:
			turretSprite = GameSprite.LASER_NORTH;
			laserSprite = GameSprite.LASER_BEAM_NS;
			break;
		case SOUTH:
			turretSprite = GameSprite.LASER_SOUTH;
			laserSprite = GameSprite.LASER_BEAM_NS;
			break;
		case WEST:
			turretSprite = GameSprite.LASER_WEST;
			laserSprite = GameSprite.LASER_BEAM_EW;
			break;
		default:
			turretSprite = GameSprite.LASER_NORTH;
			laserSprite = GameSprite.LASER_BEAM_NS;
			break;
		
		}
		
		Entity laser = createHazard("Laser Cannon", turretSprite,activeZone,x,y,true);
		
		Mappers.hazardComponent.get(laser).command = new LaserCommand(engine,laser,direction);
		Mappers.hazardComponent.get(laser).cost = 10;
		
		Point mxmy = Direction.getMxMy(direction);
		Point beamPosition = new Point(x,y).plus(mxmy);
		
		int length = 1;
		while (Mappers.zoneComponent.get(activeZone).zone.isPassable(beamPosition.x(), beamPosition.y()) 
				&& !isDoor(Mappers.zoneComponent.get(activeZone).zone,beamPosition.x(), beamPosition.y())) {
			Entity beam = createHazard("Laser Beam", laserSprite,activeZone,beamPosition.x(), beamPosition.y(), false);
			Mappers.hazardComponent.get(beam).command = new LaserCommand(engine,beam,direction);
			Mappers.hazardComponent.get(laser).cost = length++;
			beamPosition = beamPosition.plus(mxmy);
		}
		
		Gdx.app.log("createLaser:"," entity ID: " + laser.getId());
		
		return laser;
	}
	
	private void createConveyor(Direction direction, int maxLength, Entity activeZone, int x, int y) {
		
		GameSprite conveyorSprite;
		
		switch (direction) {
		case EAST:
			conveyorSprite = GameSprite.CONVEYOR_EAST;
			break;
		case NORTH:
			conveyorSprite = GameSprite.CONVEYOR_NORTH;
			break;
		case SOUTH:
			conveyorSprite = GameSprite.CONVEYOR_SOUTH;
			break;
		case WEST:
			conveyorSprite = GameSprite.CONVEYOR_WEST;
			break;
		default:
			conveyorSprite = GameSprite.CONVEYOR_NORTH;
			break;
		
		}
				
		Point mxmy = Direction.getMxMy(direction);
		Point conveyorPosition = new Point(x,y);
		int conveyors = 0;
		
		while (conveyors < maxLength && Mappers.zoneComponent.get(activeZone).zone.isPassable(conveyorPosition.x(), conveyorPosition.y())
				&& !isDoor(Mappers.zoneComponent.get(activeZone).zone,conveyorPosition.x(), conveyorPosition.y())) {
			
			Mappers.zoneComponent.get(activeZone).zone.setZoneTile(conveyorPosition.x(), conveyorPosition.y(), Zone.ZONE_LAYER.BASE.getLayer(), Tile.BLACK);
			
			Entity conveyor = createHazard("Conveyor Belt", conveyorSprite,activeZone,conveyorPosition.x(), conveyorPosition.y(), false);
			Mappers.hazardComponent.get(conveyor).command = new PushCommand(engine,conveyor,direction);
			conveyorPosition = conveyorPosition.plus(mxmy);
			conveyors++;
		}
	}
		
	private void createSmasher(Direction direction, Entity activeZone, int x, int y) {
		
		GameSprite smasherSprite,smasherBarSprite;
		
		switch (direction) {
		case EAST:
			smasherSprite = GameSprite.SMASHER_EAST;
			smasherBarSprite = GameSprite.SMASHER_BAR_EAST;
			break;
		case NORTH:
			smasherSprite = GameSprite.SMASHER_NORTH;
			smasherBarSprite = GameSprite.SMASHER_BAR_NORTH;
			break;
		case SOUTH:
			smasherSprite = GameSprite.SMASHER_SOUTH;
			smasherBarSprite = GameSprite.SMASHER_BAR_SOUTH;
			break;
		case WEST:
			smasherSprite = GameSprite.SMASHER_WEST;
			smasherBarSprite = GameSprite.SMASHER_BAR_WEST;
			break;
		default:
			smasherSprite = GameSprite.SMASHER_NORTH;
			smasherBarSprite = GameSprite.SMASHER_BAR_NORTH;
			break;
		
		}
				
		Entity smasher = createHazard("Smasher", smasherSprite,activeZone,x,y,true);
		Entity smasherBar = createHazard("Smasher", smasherBarSprite,activeZone,x,y,true);
		
		Mappers.zoneComponent.get(activeZone).zone.setZoneTile(x, y, Zone.ZONE_LAYER.BASE.getLayer(), Tile.BLACK);
		Point position = Direction.getMxMy(direction).plus(new Point(x,y));
		Mappers.zoneComponent.get(activeZone).zone.setZoneTile(position.x(), position.y(), Zone.ZONE_LAYER.FEATURE.getLayer(), Tile.BLOOD);
		
		Mappers.hazardComponent.get(smasher).command = new SmasherCommand(engine,smasher,smasherBar,direction);
		Mappers.hazardComponent.get(smasherBar).command = new DoNothingCommand();

	}
	
	private void createSpike(Direction direction, Entity activeZone, int x, int y) {
		
		GameSprite spikeSprite;
		
		switch (direction) {
		case EAST:
			spikeSprite = GameSprite.SPIKE_EAST;
			break;
		case NORTH:
			spikeSprite = GameSprite.SPIKE_NORTH;
			break;
		case SOUTH:
			spikeSprite = GameSprite.SPIKE_SOUTH;
			break;
		case WEST:
			spikeSprite = GameSprite.SPIKE_WEST;
			break;
		default:
			spikeSprite = GameSprite.SPIKE_NORTH;
			break;
		
		}
				
		Entity spike = createHazard("Spike", spikeSprite,activeZone,x,y,false);
				
		Mappers.hazardComponent.get(spike).command = new SpikeCommand(engine,spike,direction);

	}
	
private void createFire(Entity activeZone, int x, int y) {
					
		Entity fire = createHazard("Fire", GameSprite.FIRE,activeZone,x,y,false);
				
		Mappers.hazardComponent.get(fire).command = new SpikeCommand(engine,fire,Direction.NORTH);

	}

	private Entity createHazard(String name, GameSprite sprite,	Entity activeZone, int x, int y, boolean solid) {
		Entity entity = engine.createEntity();
		
		
		TransformComponent position = engine.createComponent(TransformComponent.class);
		HazardComponent hazard = engine.createComponent(HazardComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		CharacterComponent characterComponent = engine.createComponent(CharacterComponent.class);
				
		position.pos.set(x, y, 0.0f);
		position.newPos.set(x, y, 0.0f);
		position.zoneID = activeZone.getId();
		
		texture.region = sprite.getTexture();
		
		characterComponent.setName(name);
		
		if (solid) {
			BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
			bounds.bounds.width = 1;
			bounds.bounds.height = 1;
			entity.add(bounds);
		}
		
		entity.add(position);
		entity.add(hazard);
		entity.add(texture);
		entity.add(characterComponent);
		
		engine.addEntity(entity);
		
		return entity;
	}
	
	private void createRepairItem(Entity activeZone, int x, int y) {
		
			
		Entity wrench = createItem("Wrench", GameSprite.WRENCH,activeZone,x,y);
				
		Mappers.itemComponent.get(wrench).command = new RepairCommand(engine,wrench);
		
		Mappers.itemComponent.get(wrench).type = ITEM_TYPE.REPAIR;

	}
	
	private Entity createItem(String name, GameSprite sprite,	Entity activeZone, int x, int y) {
		Entity entity = engine.createEntity();
		
		
		TransformComponent position = engine.createComponent(TransformComponent.class);
		ItemComponent item = engine.createComponent(ItemComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		CharacterComponent characterComponent = engine.createComponent(CharacterComponent.class);
				
		position.pos.set(x, y, 0.0f);
		position.newPos.set(x, y, 0.0f);
		position.zoneID = activeZone.getId();
		
		texture.region = sprite.getTexture();
		
		characterComponent.setName(name);

		entity.add(position);
		entity.add(item);
		entity.add(texture);
		entity.add(characterComponent);
		
		engine.addEntity(entity);
		
		return entity;
	}
	
	
	private void createCamera(Entity target) {
		Entity entity = engine.createEntity();
		
		CameraComponent camera = engine.createComponent(CameraComponent.class);
		camera.camera = engine.getSystem(RenderingSystem.class).getGameCamera();
		camera.target = target;
		camera.offsetX = 0;
		camera.offsetY = 0;
		
		entity.add(camera);
		
		engine.addEntity(entity);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.ZONE_CHANGE) {
			if(msg.extraInfo != null && ZoneChangeMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ){
				//Entity oldZone = ((ZoneChanged)msg.extraInfo).oldZone;
				Entity newZone = ((ZoneChangeMessage)msg.extraInfo).newZone;
				setActiveZone(newZone);
				return true;
			}
		}
		return false;
	}
	
	/*
	private Entity createSector(int x, int y) {
		Entity entity = engine.createEntity();
		SectorComponent sector = new SectorComponent(SECTOR_WIDTH,SECTOR_HEIGHT,0,0);
				
		for (int w = 0; w < sector.sector.length; w++)
			for (int h = 0; h < sector.sector[w].length; h++) {
				sector.sector[w][h] = Tile.GRASS;
				sector.visible[w][h] = true;
				sector.lit[w][h] = true;
			}
		
		sector.sectorX = x;
		sector.sectorY = y;
		
		entity.add(sector);
		
		ActiveSectorComponent active = new ActiveSectorComponent();
		entity.add(active);
		
		return entity;
	}
	*/
}
