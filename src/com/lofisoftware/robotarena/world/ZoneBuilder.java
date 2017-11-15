package com.lofisoftware.robotarena.world;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.lofisoftware.robotarena.util.Line;
import com.lofisoftware.robotarena.util.Point;

public class ZoneBuilder {

	private int width, height, layers;
	private Tile[][][] tiles;
	private Zone zone;

	public ZoneBuilder(int width, int height) {
		this.width = width;
		this.height = height;
		this.layers = Zone.ZONE_DEFAULT_LAYERS;
	}

	public Zone buildRandomOverworldZone(Random rand) {
		
		createEmptyZone(width, height, layers);
		
		doCellularAutomata(Tile.FLOOR_GREEN, Tile.FLOOR_TAN, 12);
		
		addRandomFeature(Tile.RED_CPU, Tile.FLOOR_GREEN, rand, 0.10f);
		
		addRandomFeature(Tile.GREEN_CPU, Tile.FLOOR_TAN,rand,  0.10f);
		
		zone = new Zone(tiles);
		
		return zone;
	}
	
	public Zone buildDungeonZone(Random rand) {
		
		
		
		int maxFails = 50;
		
		while (maxFails-- >= 0) {
			
			createEmptyZone(width, height, layers);
			
			Map map = new Map(rand, width, height); 
			map.generate(10);
			
			for (int x=0;x<map.getWidth();x++) {
				for (int y=0;y<map.getHeight();y++) {
					int tile = map.getTile(x,y,0);
					if (tile != 0) {
						tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_FLOOR;
					}
				}
			}
			
			for (int i=1;i<map.getHighestRoomID()+1;i++) {
				Room room = map.getRoomByID(i);
				if (room != null) {
					Rectangle edges = room.getRoomDimension();
					
					for (int x = (int) edges.x;x<edges.x+edges.width;x++) {
						for (int y = (int) edges.y;y<edges.y+edges.height;y++) {
							if (x == edges.x || y == edges.y || x == edges.x+edges.width-1 || y == edges.y+edges.height-1)
								tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_WALL;
						}
					}
					
					for (int d=0;d<room.getDoorCount();d++) {
						Door door = room.getDoor(d);
						
						tiles[door.getAX()][door.getAY()][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_DOOR_OPEN;
						tiles[door.getBX()][door.getBY()][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_DOOR_OPEN;
						
					}
				}
			}
			
			//printMap(map);
			
			zone = new Zone(tiles);
			zone.setMap(map);
			Rectangle startingArea = getRandomRoom().getRoomDimension();
			
			while (startingArea.width < 14)
				startingArea = getRandomRoom().getRoomDimension();
			
			zone.setStartingArea(startingArea);
			int farRoom = zone.floodFillMapFromPoint(1,MathUtils.round(startingArea.x+(startingArea.width/2)),MathUtils.round(startingArea.y+(startingArea.height/2)), true);
			zone.setFinishingArea(zone.getMap().getRoomByID(farRoom).getRoomDimension());
	
			Gdx.app.log("GameEngine:buildDungeonZone","Total Rooms: " + map.getTotalRooms());
			
			//addRandomFeature(Tile.SMALL_SKULL, Tile.DUNGEON_FLOOR, rand, 0.03f);
			// addRandomFeature(Tile.GREEN_CPU, Tile.DUNGEON_FLOOR, rand, 0.03f);
			
			zone.floodFillMapFromPoint(1,MathUtils.round(startingArea.x+(startingArea.width/2)),MathUtils.round(startingArea.y+(startingArea.height/2)), false);
			if (zone.isStartConnectedToFinish())
				return zone;
		}
		
		Gdx.app.error("ZoneBuilder:buildDungeonZone", "No connectted Dungeon could be found");
		return null;
	}
	
	private boolean isTilePassable(int x, int y) {
		if (isValidTile(x,y) && tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()].isPassable()
				&& tiles[x][y][Zone.ZONE_LAYER.FEATURE.getLayer()].isPassable()
				&& tiles[x][y][Zone.ZONE_LAYER.DECORATION.getLayer()].isPassable())
			return true;
		return false;
	}
	
	public Room getRandomRoom() {
		Room room = zone.getMap().getRoomByID(zone.getMap().getRandomValidRoomID());
		Gdx.app.log("GameEngine:randomRoom","Random Room: id(" + room.getId() + ") " + room.getRoomDimension() );
		return room;
	}
	
	public Zone buildDragStrip(Random rand){
		
		createEmptyZone(width, height, layers);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				
				if (isTileEdge(x,y))
					tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_WALL;
				else
					tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_FLOOR;
				
			}
		}
		
		addRandomFeature(Tile.SMALL_SKULL, Tile.DUNGEON_FLOOR, rand, 0.03f);
		addRandomFeature(Tile.DUNGEON_WALL, Tile.DUNGEON_FLOOR, rand, 0.03f);
		
		if (width > height) {
			addTextLine(new Point(5,height-2), new Point(5,1),  Tile.FLOOR_BIG_CHECK, "START");
			addTextLine(new Point(width-5,height-2), new Point(width-5,1), Tile.FLOOR_SMALL_CHECK, "FINISH");
		}
		else {
			addTextLine(new Point(1,height-5), new Point(width-2,height-5), Tile.FLOOR_BIG_CHECK, "START");
			addTextLine(new Point(1,5), new Point(width-2,5), Tile.FLOOR_SMALL_CHECK, "FINISH");
		}
		
		zone = new Zone(tiles);
		
		return zone;
	}

	public void addTextLine(Point one, Point two, Tile tile, String text) {
		
		addTextLine(addLine(one, two, tile), text);
		
	}
	
	public void addTextLine(Line line, String text) {
		
		if (line.getPoints().size() >= text.length()) {
			int pointNumber = 0;
			int characterNumber = 0;
			int spacing = MathUtils.round((float)line.getPoints().size() / text.length());
			//Gdx.app.log("ZoneBuilder:addTextLine", "spacing:" + spacing);
			
			for (Point p : line) {
				if (isValidTile(p.x(),p.y()) && pointNumber%spacing == 0 && characterNumber < text.length()) {
					tiles[p.x()][p.y()][Zone.ZONE_LAYER.FEATURE.getLayer()] = Tile.getTileByName(text.substring(characterNumber, ++characterNumber));
				}
				pointNumber++;
			}
			
		}
		else
			Gdx.app.error("ZoneBuilder:addTextLine", "Line not long enough for Text");
	}
	
	public Line addLine(Point one, Point two, Tile tile) {
		
		Line line = new Line(one.x(),one.y(),two.x(),two.y());
		
		for (Point p : line) {
			if (isValidTile(p.x(),p.y())) {
				tiles[p.x()][p.y()][Zone.ZONE_LAYER.BASE.getLayer()] = tile;
			}
		}
		
		return line;
		
	}
	
	public Zone buildGridDungeon(int cellsX, int cellsY, Random rand) {
		
			int cellSizeX = width/cellsX;
			int cellSizeY = height/cellsY;
			
			if (cellSizeX < 4 || cellSizeY < 4)
				return null;
			
			createEmptyZone(width, height, layers);
			
			for (int x = 0; x < cellsX; x++) {
				for (int y = 0; y < cellsY; y++) {
					buildGridDungeonRoom(x*cellSizeX,y*cellSizeY,cellSizeX,cellSizeY);
				}
			}
			
			addRandomFeature(Tile.SMALL_SKULL, Tile.DUNGEON_FLOOR, rand, 0.03f);
			
			zone = new Zone(tiles);
			
			return zone;
	}
	
	public void buildGridDungeonRoom(int originX, int originY, int width, int height) {
		
		for (int x = originX; x < originX + width; x++) {
			for (int y = originY; y < originY + height; y++) {
				if(x == originX || y == originY 
						|| x == originX + width-1 || y == originY + height-1) {
						
					if (isTileEdge(x,y))
						tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_WALL;
					else if (x == originX + MathUtils.ceil((float) ((width*0.5)-1))  || y == originY + MathUtils.ceil((float) ((height*0.5)-1) ))
						tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_FLOOR;
					else
						tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_WALL;
				}
				else
					tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] = Tile.DUNGEON_FLOOR;					
				
			}
		}

		
		
	}

	private boolean isTileEdge(int x, int y) {
		if (x == 0 || y == 0 || x == tiles.length-1 || y == tiles[x].length-1)
			return true;
		return false;
	}
	
	private boolean isValidTile(int x, int y) {
		if (x >= 0 && y >= 0 && x < tiles.length && y < tiles[x].length)
			return true;
		return false;
	}

	private void addFeature(Tile feature, int x, int y) {
				
		if (isValidTile(x, y)) {
				tiles[x][y][Zone.ZONE_LAYER.FEATURE.getLayer()] = feature;					
		}

	}
	
	private void addRandomFeature(Tile feature, Tile baseTile, Random rand, float chance) {
		
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				
				if (tiles[x][y][Zone.ZONE_LAYER.BASE.getLayer()] == baseTile 
						&& rand.nextDouble() < chance)
					tiles[x][y][Zone.ZONE_LAYER.FEATURE.getLayer()] = feature;					
			}
		}
		//Gdx.app.log("ZoneBuilder: ", "addRandomFeature: " + feature.getName());
	}

	public Tile[][][] createEmptyZone(int zoneWidth, int zoneHeight, int zoneLayers) {
		
		tiles = new Tile [zoneWidth] [zoneHeight] [zoneLayers]; 
		
		Tile type = Tile.EMPTY;
		
		for (int x = 0; x < zoneWidth; x++) {
			for (int y = 0; y < zoneHeight; y++) {
				for (int z = 0; z < zoneLayers; z++) {
					tiles[x][y][z] = type;					
				}
			}
		}
		return tiles;
	}
	
	public void doCellularAutomata(Tile tile1, Tile tile2, int times) {
		randomizeTiles(tile1, tile2, Zone.ZONE_LAYER.BASE.getLayer());
		smooth(tile1, tile2, Zone.ZONE_LAYER.BASE.getLayer(), times);
	}

	public ZoneBuilder fillTile(Tile tile) {
		return fillTile(tile,0);
	}
	
	public ZoneBuilder fillTile(Tile tile, int z) {		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y][z] = tile;
			}
		}
		//Gdx.app.log("ZoneBuilder: ", "FillTile: " + tile.getName());
		return this;
	}

	/*
	public ZoneBuilder fillGrassAndDesert() {
		
		int z = 0;
		Tile tile = Tile.GRASS;
		
		this.zone = new Tile[width][height][layers];
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < MapTools.MAP_SECTOR_HEIGHT; y++) {
				for (int x2 = 0; x2 < MapTools.SECTOR_TILE_WIDTH; x2++) {
					for (int y2 = 0; y2 < MapTools.SECTOR_TILE_HEIGHT; y2++) {
							zone[(x*MapTools.SECTOR_TILE_WIDTH)+x2][(y*MapTools.SECTOR_TILE_HEIGHT)+y2][z] = tile;
							
							if((tile == Tile.GRASS) && Math.random() < 0.3)
								zone[(x*MapTools.SECTOR_TILE_WIDTH)+x2][(y*MapTools.SECTOR_TILE_HEIGHT)+y2][z+1] = Tile.TREE;
							if((tile == Tile.DESERT) && Math.random() < 0.3)
								zone[(x*MapTools.SECTOR_TILE_WIDTH)+x2][(y*MapTools.SECTOR_TILE_HEIGHT)+y2][z+1] = Tile.CACTUS;								
					}					
				}
				tile = (tile == Tile.GRASS)?Tile.DESERT:Tile.GRASS;
			}
			tile = (tile == Tile.GRASS)?Tile.DESERT:Tile.GRASS;
		}
		return this;
	}
	*/
	
	private ZoneBuilder randomizeTiles(Tile tile1, Tile tile2, int layer) {
		for (int x = 0; x < width; x++) { 
			for (int y = 0; y < height; y++) {
				tiles[x][y][layer] = Math.random() < 0.5 ? tile1 : tile2;
			}
		}
		//Gdx.app.log("ZoneBuilder: ", "Random");
		return this;
	}

	private ZoneBuilder smooth(Tile tile1, Tile tile2, int layer, int times) {
		Tile[][] tiles2 = new Tile[width][height];
		for (int time = 0; time < times; time++) {

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int tileCnt1 = 0;
					int tileCnt2 = 0;

					for (int ox = -1; ox < 2; ox++) {
						for (int oy = -1; oy < 2; oy++) {
							if (x + ox < 0 || x + ox >= width || y + oy < 0
									|| y + oy >= height)
								continue;

							if (tiles[x + ox][y + oy][layer] == tile1)
								tileCnt1++;
							else
								tileCnt2++;
						}
					}
					tiles2[x][y] = tileCnt1 >= tileCnt2 ? tile1 : tile2;
				}
			}
			
			copyLayer(tiles2, layer);
		}
		return this;
	}

	private void copyLayer(Tile[][] tilesB, int level) {
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[x].length;y++)
				tiles[x][y][level]=tilesB[x][y];
		
		//Gdx.app.log("ZoneBuilder: ", "Smooth");
		
	}

}
