package com.lofisoftware.robotarena.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

public class FieldOfView {
	
	private boolean [] [] remembered;
	private boolean [] [] visible;
	private int width, height;
	private Array<Entity> entities;
	private Array<Entity> items;
	
	public FieldOfView(int width, int height) {
		initialize(width, height);
	}
	
	private void initialize(int width, int height) {
		this.width = width;
		this.height = height;
		
		remembered = new boolean [width] [height];
		visible = new boolean [width] [height];
		entities = new Array<Entity>(false, 16);
		items = new Array<Entity>(false, 8);
	}
	
	public boolean isValid(int x, int y) {
		if (remembered == null  || visible == null || x < 0 || y < 0)
			return false;
		if (x >= width || y >= height)
			return false;
		
		return true;
	}
	
	public void resetVisible(boolean value) {
		if (visible != null) {
			for (int x = 0; x <  width; x++) {
				for (int y = 0; y <  height; y++) {
					visible[x][y] = value;
				}
			}
		}
	}

	public boolean isVisible(int x, int y) {
		if (isValid(x,y))
			return visible[x][y];
		return false;
	}

	public void setVisible(int x, int y, boolean value) {
		if (isValid(x,y))
			visible[x][y] = value;
	}

	public boolean isRemembered(int x, int y) {
		if (isValid(x,y))
			return remembered[x][y];
		return false;
	}

	public void setRemembered(int x, int y, boolean value) {
		if (isValid(x,y))
			remembered[x][y] = value;
	}
	
	public Array<Entity> getEntityList() {
		return entities;
	}
	
	public void resetEntityList() {
		entities.clear();
	}
	
	public boolean addEntity(Entity entity) {
		if (entities != null && !entities.contains(entity, true)) {
			entities.add(entity);
			return true;
		}
		return false;
	}
	
	public boolean containsEntity(Entity entity) {
		if (entities == null) 
			return false;
		else
			return entities.contains(entity, true);
	}

	public void addItem(Entity itemAt) {
		items.add(itemAt);
		
	}

	public void resetItemList() {
		items.clear();
	}
	
	public Array<Entity> getItemList() {
		return items;
	}
	
}
