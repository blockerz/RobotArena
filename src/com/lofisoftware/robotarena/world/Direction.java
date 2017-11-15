package com.lofisoftware.robotarena.world;

import com.badlogic.gdx.math.MathUtils;
import com.lofisoftware.robotarena.util.Point;

public enum Direction {
	
	NORTH(0),
	EAST(90),
	SOUTH(180),
	WEST(270);

	public int rotation; 
	
	Direction(int rotation) {
		this.rotation = rotation;
	}
	
	//public static final int NORTH = 0;
	//public static final int EAST = 90;
	//public static final int SOUTH = 180;
	//public static final int WEST = 270;
	
	public static Direction randomDirection(){
		
		switch(MathUtils.random(0, 3)){
		case 0:
			return Direction.SOUTH;
		case 1:
			return Direction.NORTH;
		case 2:
			return Direction.WEST;
		case 3:
			return Direction.EAST;
		}
		return null;
	}

	public String direction() {
		
		switch(this) {
		case NORTH:
			return "North";
		case SOUTH:
			return "South";			
		case EAST:
			return "East";			
		case WEST:
			return "West";
		}
		return "Unknown";		
	}
	
	public static Direction opposite(Direction direction) {
		Direction opposite;
		
		switch(direction) {
		case NORTH:
			opposite = Direction.SOUTH;
			break;
		case SOUTH:
			opposite = Direction.NORTH;
			break;
		case EAST:
			opposite = Direction.WEST;
			break;
		case WEST:
			opposite = Direction.EAST;
			break;
			default:
				opposite = null;
		}
		return opposite;		
	}
	
	public static Point getMxMy(Direction direction) {
		Point mxmy = new Point(0,0);
		
		switch(direction) {
		case NORTH:
			mxmy.set(0, 1);
			break;
		case SOUTH:
			mxmy.set(0, -1);
			break;
		case EAST:
			mxmy.set(1, 0);
			break;
		case WEST:
			mxmy.set(-1, 0);
			break;
			default:
				break;
		}
		return mxmy;		
	}
	
	public static Direction rotate90(Direction currentDirection, Direction desiredDirection) {
		
		Direction rotate = currentDirection;
		
		switch (currentDirection) {
		case NORTH:
			switch (desiredDirection) {
			case SOUTH:
			case EAST:
				rotate = Direction.EAST;
				break;
			case WEST:
				rotate = Direction.WEST;
				break;
			case NORTH:
				break;
			default:
				break;
			}
			break;
		case EAST:
			switch (desiredDirection) {
			case WEST:
			case NORTH:
				rotate = Direction.NORTH;
				break;
			case SOUTH:
				rotate = Direction.SOUTH;
				break;
			case EAST:
				break;
			default:
				break;
			}
			break;
		case SOUTH:
			switch (desiredDirection) {
			case NORTH:
			case EAST:
				rotate = Direction.EAST;
				break;
			case WEST:
				rotate = Direction.WEST;
				break;
			case SOUTH:
				break;
			default:
				break;
			}
			break;
		case WEST:
			switch (desiredDirection) {
			case EAST:
			case NORTH:
				rotate = Direction.NORTH;
				break;
			case SOUTH:
				rotate = Direction.SOUTH;
				break;
			case WEST:
				break;
			default:
				break;
			}
			break;
		}
		
		return rotate;
	}	
	
	public static Direction getXYDirection(int mx, int my){
		
		if (mx == 0 && my > 0)
			return NORTH;
		
		if (mx == 0 && my < 0)
			return SOUTH;
		
		if (my==0 && mx < 0)
			return WEST;
		
		if (my==0 && mx > 0)
			return EAST;
		
		if (mx > 0 && my > 0)
			return NORTH;
		
		if (mx < 0 && my < 0)
			return SOUTH;
		
		if (my>0 && mx < 0)
			return WEST;
		
		if (my<0 && mx > 0)
			return EAST;
		
		return null;
	}

}