package com.lofisoftware.robotarena.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.lofisoftware.robotarena.systems.CollisionSystem;
import com.lofisoftware.robotarena.systems.HazardSystem;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.util.AStarPathFinder;
import com.lofisoftware.robotarena.util.Mover;
import com.lofisoftware.robotarena.util.Path;
import com.lofisoftware.robotarena.util.TileBasedMap;
import com.lofisoftware.robotarena.world.Zone;

public class PathFindingSkill implements TileBasedMap, Mover {
	
	public static final int MAX_SEARCH = 25;
	
	Zone zone;
	AStarPathFinder pathFinder;
	PooledEngine engine;
	long zoneId;
	Entity self;
	Entity targetEntity;
	
	public PathFindingSkill(PooledEngine engine, Entity self, Entity targetEntity, Zone zone, long zoneId) {
		this.zone = zone;
		this.engine = engine;
		this.zoneId = zoneId;
		this.self = self;
		this.targetEntity = targetEntity;
		pathFinder = new AStarPathFinder(this,MAX_SEARCH,false);
	}

	@Override
	public int getWidthInTiles() {
		return zone.getWidth(); 
	}

	@Override
	public int getHeightInTiles() {
		return zone.getHeight();
	}

	@Override
	public void pathFinderVisited(int x, int y) {
		//Gdx.app.log("PathFindingSkill:pathfinderVisited","Path: " + x + ", " + y);
	}

	@Override
	public boolean blocked(Mover mover, int x, int y) {
		
		if (!zone.isPassable(x, y))
			return true;
		
		Entity entity = engine.getSystem(CollisionSystem.class).getEntityAt(x, y, zoneId);
		
		if (entity != null && entity.getId() != self.getId()) {
			if (targetEntity == null || entity.getId() != targetEntity.getId())
				return true;
		}
		
		return  false ;
	}

	@Override
	public float getCost(Mover mover, int sx, int sy, int tx, int ty) {
		
		Entity entity = engine.getSystem(HazardSystem.class).getHazardsAt(tx, ty, zoneId);
		
		// avoid hazards but keep them passable
		if (entity != null && entity.getId() != self.getId()) {
			//Gdx.app.log("PathFindingSkill:blocked", "entity blocked");
			return 20+Mappers.hazardComponent.get(entity).cost;
		}
		
		return 1;
	}

	@Override
	public boolean canEnter(int x, int y) {
		return !zone.isPassable(x, y);
	}

	public Path findPath(int sx, int sy, int tx, int ty) {
		return pathFinder.findPath(this, sx, sy, tx, ty); 
	}
}
