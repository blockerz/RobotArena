package com.lofisoftware.robotarena.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lofisoftware.robotarena.commands.MoveCommand;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;
import com.lofisoftware.robotarena.systems.CompetitorSystem;
import com.lofisoftware.robotarena.systems.HazardSystem;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.util.Path;
import com.lofisoftware.robotarena.util.Point;
import com.lofisoftware.robotarena.world.Direction;
import com.lofisoftware.robotarena.world.Map;
import com.lofisoftware.robotarena.world.Zone;

public class OpponentBrain implements Telegraph {

	public static final float ATTACK_DISTANCE = 1;

	private StateMachine<OpponentBrain> stateMachine;
	
	private Entity self;
	private Entity zoneEntity;
	private Entity target;
	PooledEngine engine;
	Path path;
	int pathStep = 0;
	
	// 0.0 to 1.0
	private float aggressiveness = 0.3f;
	
	TransformComponent transformComponent;
	ZoneComponent zoneComponent;
	
	public OpponentBrain(PooledEngine engine, Entity self, Entity zone) {
		this.engine = engine;
		this.self = self;
		this.zoneEntity = zone;
		target = null;
		stateMachine = new DefaultStateMachine<OpponentBrain>(this, OpponentState.RACE);
		transformComponent = Mappers.transformComponent.get(self);
		zoneComponent = Mappers.zoneComponent.get(zone);
	}

	public StateMachine<OpponentBrain> getStateMachine () {
		return stateMachine;
	}
	
	@Override
	public boolean handleMessage (Telegram msg) {
		return stateMachine.handleMessage(msg);
	}

	public void update (float delta) {
		stateMachine.update();
	}

	public void attackTarget() {
		
		//target = engine.getSystem(CompetitorSystem.class).getNearestCompetitorInView(transformComponent.newPos, ATTACK_DISTANCE, self);
		
		if (target != null && Mappers.transformComponent.has(target)) {
			
			moveTowardPosition(MathUtils.floor(Mappers.transformComponent.get(target).newPos.x), MathUtils.floor(Mappers.transformComponent.get(target).newPos.y),true);
				
		}
			
		Gdx.app.log("OpponentBrain:attackTarget",Mappers.characterComponent.get(self).getName() + ": I'm going to attack. ");	
	}
	
	public boolean moveTowardPosition (int x, int y, boolean refresh) {
		
		PathFindingSkill pathFindingSkill = new PathFindingSkill(engine, self, target, zoneComponent.zone, zoneComponent.zoneID);
		
		if (path == null || refresh || pathStep >= path.getLength()-1) {
			path = pathFindingSkill.findPath(MathUtils.floor(transformComponent.newPos.x), MathUtils.floor(transformComponent.newPos.y), x, y);
			pathStep = 1;
		}
		
		if (path != null) {
	
			if (path != null && path.getLength() > 1) {
			    int mx = path.getX(pathStep) - MathUtils.floor(transformComponent.newPos.x);
			    int my = path.getY(pathStep) - MathUtils.floor(transformComponent.newPos.y);
			    pathStep++;
		
			    move(mx,my);
			    return true;
			}
			else {
				wander();
			}
		}
		else {
			wander();
		}
		return false;
	}
	
	public boolean isThreatenedByCompetitor() {
		
		
/*		if (transformComponent != null && zoneComponent != null && Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID) != null) {
			return Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID).containsEntity(player);
		}*/
    	
		target = engine.getSystem(CompetitorSystem.class).getNearestCompetitorInView(transformComponent.newPos, ATTACK_DISTANCE, self);
		
		if (target != null && MathUtils.random() < aggressiveness) {
			Gdx.app.log("OpponentBrain:isThreatenedByCompetitor",Mappers.characterComponent.get(self).getName() + ": My rival is near and must be dealt with. ");
			return true;
		}
		
		/*if (transformComponent != null && target != null && transformComponent.zoneID == Mappers.transformComponent.get(target).zoneID) {
			
			float distance = transformComponent.newPos.dst( Mappers.transformComponent.get(target).newPos);

			if (distance <= 10)
				return true;
		}*/
		return false;
	}
	
	public boolean isThreatenedByEnemy() {
		
		float distance = getNearestEnemyInView();
		
		if (target != null && distance <= (aggressiveness*5) + 1.1f) {
			Gdx.app.log("OpponentBrain:isThreatenedByEnemy",Mappers.characterComponent.get(self).getName() + ": An enemy is near and must be dealt with. ");
			return true;
		}
		
		/*if (transformComponent != null && target != null && transformComponent.zoneID == Mappers.transformComponent.get(target).zoneID) {
			
			float distance = transformComponent.newPos.dst( Mappers.transformComponent.get(target).newPos);

			if (distance <= 10)
				return true;
		}*/
		return false;
	}

	public float getNearestEnemyInView() {
		
		float distance = 10000, closest = 10000;
		//Entity nearest = null;
		
		if (transformComponent != null && zoneComponent != null && Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID) != null) {
			Array<Entity> entities =  Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID).getEntityList();
			
			for (Entity entity : entities) {
				if (self != null && self.getId() == entity.getId())
					continue;
				
				if (Mappers.enemyComponent.has(entity)) {
					
					distance = Mappers.transformComponent.get(entity).newPos.dst(transformComponent.newPos);
					
					if (distance < closest) {
						target = entity;
						closest = distance;
					}
				}
			}
		}
		
		return distance;
	}
	
	public void race(boolean refresh){
		
		Zone zone = Mappers.zoneComponent.get(zoneEntity).zone;
		
		Point currentPosition = new Point (MathUtils.floor(transformComponent.newPos.x), MathUtils.floor(transformComponent.newPos.y));
		int currentScore = zone.getMap().getTile(currentPosition.x(),currentPosition.y(), Map.PATHING_LAYER);
		
		Point nextPosition = currentPosition.copy();
		
		int movesAhead = 3;
		
		for (int moves = 0; moves < movesAhead; moves++) {
			for (Point p : nextPosition.neighbors4()) {
				if (zone.getMap().getTile(p.x(),p.y(), Map.PATHING_LAYER) < currentScore && zone.getMap().getTile(p.x(),p.y(), Map.PATHING_LAYER) >= 0) {
					nextPosition = p.copy();
					break;
				}
			}
			if (engine.getSystem(HazardSystem.class).getHazardsAt(nextPosition.x(), nextPosition.y(), zoneComponent.zoneID) != null) {				
				// if we stop on a hazard go a little further for path			
				if (moves == movesAhead-1 && movesAhead < 10) {
						movesAhead++;
				}
			}
			else if (moves == 0) {
				move(nextPosition.x()-currentPosition.x(),nextPosition.y()-currentPosition.y());
				return;
			}
		}

		moveTowardPosition(nextPosition.x(),nextPosition.y(), refresh);
		
	    //move(nextPosition.x()-currentPosition.x(),nextPosition.y()-currentPosition.y());
	}
	
	public void wander(){
		
		Direction direction = Direction.randomDirection();

	    move(Direction.getMxMy(direction).x(),Direction.getMxMy(direction).y());
	}

	public void move(int mx, int my) {
		
		// TODO: This should not be needed but errors sometimes happen with AI picking 0,0
		if (mx == 0 && my == 0) {
			wander();
			return;
		}
		
		Direction direction = Direction.getXYDirection(mx, my); 
		
		new MoveCommand(engine, self, direction).execute();
		
		Gdx.app.log("OpponentBrain:move"," I'm going to move " + mx + ", " + my);
		
	   /* MovementComponent movement = engine.createComponent(MovementComponent.class);
		movement.animate = true;
		movement.animationTime = 0.25f;
		//movement.move.set(deltaX,deltaY,0);
		movement.move.set(mx,my,0);
		self.add(movement);
		Gdx.app.log("EnemyBrain:move"," I'm going to move " + mx + ", " + my);
		Mappers.stateComponent.get(self).set(STATE.MOVING);
		
		MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_MOVED, new EntityMovedMessage(self));*/	
	}

	public void sleep() {

		
	}

	public float distanceToTarget() {
		if (target != null && Mappers.transformComponent.has(target))
			return Mappers.transformComponent.get(target).newPos.dst(transformComponent.newPos);
		
		return 10000f;
	}

	public boolean isNeededItemNear() {

		if (transformComponent != null && zoneComponent != null && Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID) != null) {
			Array<Entity> items =  Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID).getItemList();
			
			for (Entity item : items) {
				
				if (Mappers.itemComponent.has(item)) {
					
					switch (Mappers.itemComponent.get(item).type) {
					case REPAIR:
						if (Mappers.statsComponent.get(self).getCurrentHealth() < Mappers.statsComponent.get(self).getMaxHealth()) {
							target = item;
							return true;
						}
					case SPECIAL:
						break;
					default:
						break;
					
					}
				}
			}
		}
		
		return false;
	}
}