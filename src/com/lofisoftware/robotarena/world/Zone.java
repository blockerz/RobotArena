package com.lofisoftware.robotarena.world;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.lofisoftware.robotarena.util.Point;

public class Zone {

	public static final int ZONE_DEFAULT_WIDTH = 100;
	public static final int ZONE_DEFAULT_HEIGHT = 100;
	public static final int ZONE_DEFAULT_LAYERS = 3;
	
	public static enum ZONE_LAYER { 
		BASE(0),
		FEATURE(1), 
		DECORATION(2);
		
		private int layer;
		
		ZONE_LAYER(int layer) {
			this.layer = layer;
		}

		public int getLayer() {
			return layer;
		}
	};
	
	private Tile [][][] zone;
	private int width, height, layers;
	private Map map;
	private Rectangle startingArea;
	private Rectangle finishingArea;
	private IntMap<Rectangle> rooms;
	
	//private boolean [] [] visible;
	//private boolean [] [] lit;
	
	//public ArrayMap<Vector3,Zone> connections; 
	
	public Zone(){
		this(ZONE_DEFAULT_WIDTH,ZONE_DEFAULT_HEIGHT,ZONE_DEFAULT_LAYERS);
	}
	
	public Zone(int width, int height, int layers){
		this.width = width;
		this.height = height;
		this.layers = layers;
		
		zone = new Tile [width][height][layers];
		initialize();

	}
	
	public Zone(Tile[][][] tiles){
		this.width = tiles.length;
		this.height = tiles[0].length;
		this.layers = tiles[0][0].length;
		
		zone = tiles;
		initialize();
	}
	
	private void initialize() {
		rooms = new IntMap<Rectangle>();
	}
	
	public Tile getZoneTile(int x, int y, int z) {
		if (isValidZoneLocation(x, y, z))
			return zone[x][y][z];
		return null;
	}
	
	public boolean isValidZoneLocation(int x, int y) {
		return isValidZoneLocation(x, y, 0);
	}
	
	public boolean isValidZoneLocation(int x, int y, int z) {
		if (zone == null || x < 0 || y < 0 || z < 0)
			return false;
		if (x >= zone.length || y >= zone[x].length || z >= zone[x][y].length)
			return false;
		
		return true;
	}
	
	/*public boolean isLit(int x, int y) {
		if (x >=0 && x < lit.length)
			if (y >= 0 && y <= lit[x].length)
				return lit[x][y];
		return false;
	}

	public void setLit(int x, int y, boolean tilelit) {
		if (x >=0 && x < lit.length)
			if (y >= 0 && y <= lit[x].length)
				lit[x][y] = tilelit;
	}

	public boolean isVisible(int x, int y) {
		if (x >=0 && x < visible.length)
			if (y >= 0 && y <= visible[x].length)
				return visible[x][y];
		return false;
	}

	public void setVisible(int x, int y, boolean tileVisible) {
		if (x >=0 && x < visible.length)
			if (y >= 0 && y <= visible[x].length)
				visible[x][y] = tileVisible;
	}
*/

	public boolean setZoneTile(int x, int y, int z, Tile tile) {
		if (zone == null || x < 0 || y < 0 || z < 0)
			return false;
		if (x >= zone.length || y >= zone[x].length || z >= zone[x][y].length)
			return false;
		 zone[x][y][z] = tile;
		 return true;
	}

	public boolean isPassable(int x, int y) {
		
		if (!isValidZoneLocation(x,y)
				|| !getZoneTile(x,y,Zone.ZONE_LAYER.BASE.getLayer()).isPassable() 
				|| !getZoneTile(x,y,Zone.ZONE_LAYER.FEATURE.getLayer()).isPassable()
				|| !getZoneTile(x,y,Zone.ZONE_LAYER.DECORATION.getLayer()).isPassable())
			return false;
		
		return true;
	}
	
	public void setZone(Tile[][][] zone) {
		this.zone = zone;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getLayers() {
		return layers;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public Rectangle getStartingArea() {
		return startingArea;
	}

	public void setStartingArea(Rectangle startingArea) {
		this.startingArea = startingArea;
	}

	public Rectangle getFinishingArea() {
		return finishingArea;
	}

	public void setFinishingArea(Rectangle finishingArea) {
		this.finishingArea = finishingArea;
	}

	public boolean isStartConnectedToFinish() {
		
		//fillRegion(1,MathUtils.round(startingArea.x+(startingArea.width/2)),MathUtils.round(startingArea.y+(startingArea.height/2)));
		
		if (map.getTile(MathUtils.round(startingArea.x+(startingArea.width/2)),MathUtils.round(startingArea.y+(startingArea.height/2)), Map.FILL_LAYER) 
				== map.getTile(MathUtils.round(finishingArea.x+(finishingArea.width/2)),MathUtils.round(finishingArea.y+(finishingArea.height/2)), Map.FILL_LAYER) ) {
			Gdx.app.log("ZoneBuilder:isStartConnectedToFinish", "Start and Finish are connected.");
			return true;
		}
		
		return false;
	}
	
	public int floodFillMapFromPoint(int region, int x, int y, boolean updateRoomMap) {
        int size = 1;
        int furthestRoomID = 0;
        
        for (int x2 = 0; x2 < width; x2++) {
			for (int y2 = 0; y2 < height; y2++) {
				map.setTile(x2, y2, Map.FILL_LAYER, 0);
			}
		}
        
        ArrayList<Point> open = new ArrayList<Point>();
        open.add(new Point(x,y));
        map.setTile(x, y, Map.FILL_LAYER, region);
    
        while (!open.isEmpty()){
            Point p = open.remove(0);

            for (Point neighbor : p.neighbors4()){
                if (map.getTile(neighbor.x(), neighbor.y(), Map.FILL_LAYER) > 0
                  || !isPassable(neighbor.x(), neighbor.y()))
                    continue;

                size++;
                map.setTile(neighbor.x(), neighbor.y(),Map.FILL_LAYER, region);
                open.add(neighbor);
                
                // fill room list while we are here
                int id = map.getTile(neighbor.x(), neighbor.y(),Map.ID_LAYER);
                if (updateRoomMap && !rooms.containsKey(id) && map.getRoomByID(id) != null) {
                	rooms.put(id, map.getRoomByID(id).getRoomDimension());
                	Gdx.app.log("ZoneBuilder:fillRegion", "Added room id:" + id);
                	furthestRoomID = id;
                }
            }
        }
        return furthestRoomID;
    }

	public int calculatePaths (int x, int y) {
	
		final int startingValue = -1;
		
		int maxDistance = 0, currentDistance = 0, breakLoop = 0;
		
        for (int x2 = 0; x2 < width; x2++) {
			for (int y2 = 0; y2 < height; y2++) {
				map.setTile(x2, y2, Map.PATHING_LAYER, startingValue);
			}
		}
        
        Array<Point> frontier = new Array<Point>();
        
        Point currentPoint = new Point(x,y);
        
        frontier.add(currentPoint);
        map.setTile(currentPoint.x(), currentPoint.y(), Map.PATHING_LAYER, currentDistance);
        
        //Gdx.app.log("calculatePaths","Current Point: " + currentPoint.x() + ", " + currentPoint.y() + " frontier size: " + frontier.size);
        
        while (frontier.size > 0 && breakLoop++ < 100000) {
        	currentPoint = frontier.removeIndex(0);
        	currentDistance = map.getTile(currentPoint.x(), currentPoint.y(), Map.PATHING_LAYER);
        	//Gdx.app.log("calculatePaths","Current Point: " + currentPoint.x() + ", " + currentPoint.y() + " frontier size: " + frontier.size);
        	for (Point neighbor : currentPoint.neighbors4()) {
        		 if ( map.getTile(neighbor.x(), neighbor.y(), Map.PATHING_LAYER) != startingValue )
        		 	continue;
        		 
        		 if (!isPassable(neighbor.x(), neighbor.y()))
        				 continue;
        		 
    			 //Gdx.app.log("calculatePaths","Neighbor Point: " + neighbor.x() + ", " + neighbor.y() + " frontier size: " + frontier.size);
    		 
    			 map.setTile(neighbor.x(), neighbor.y(), Map.PATHING_LAYER, 1 + currentDistance);
    			 frontier.add(neighbor);
    		
    			 if (1 + currentDistance > maxDistance)
    				 maxDistance = currentDistance + 1;	
        		 
        	}
        }
		
		return maxDistance; 
	}

	public IntMap<Rectangle> getRooms() {
		return rooms;
	}
	
	public void printMap(int layer) {
		String message;
		int tile;
		for (int y = map.getHeight()-1; y >= 0; y--) {
			message = "";
			for (int x = 0; x < map.getWidth()-1;x++){
				tile = map.getTile(x, y, layer);
				if (tile <= 0)
					message = message.concat("|   ");
				else if (tile < 10)
					message = message.concat("|  "+tile);
				else if (tile < 100)
					message = message.concat("| "+tile);
				else
					message = message.concat("|"+tile);
			}
			Gdx.app.log("Map: ", message);
		}
		
	}
}
