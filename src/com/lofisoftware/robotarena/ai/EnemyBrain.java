package com.lofisoftware.robotarena.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.MathUtils;
import com.lofisoftware.robotarena.commands.MoveCommand;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.components.ZoneComponent;
import com.lofisoftware.robotarena.systems.CompetitorSystem;
import com.lofisoftware.robotarena.systems.Mappers;
import com.lofisoftware.robotarena.util.Path;
import com.lofisoftware.robotarena.world.Direction;

public class EnemyBrain implements Telegraph {

	public static int ATTACK_DISTANCE = 6;
	
	private StateMachine<EnemyBrain> stateMachine;
	
	private Entity self;
	private Entity zone;
	PooledEngine engine;
	PathFindingSkill pathFindingSkill; 
	
	TransformComponent transformComponent;
	ZoneComponent zoneComponent;
	
	public EnemyBrain(PooledEngine engine, Entity self, Entity zone) {
		this.engine = engine;
		this.self = self;
		this.zone = zone;
		stateMachine = new DefaultStateMachine<EnemyBrain>(this, EnemyState.SLEEP);
		transformComponent = Mappers.transformComponent.get(self);
		zoneComponent = Mappers.zoneComponent.get(zone);
		
	}

	public StateMachine<EnemyBrain> getStateMachine () {
		return stateMachine;
	}
	
	@Override
	public boolean handleMessage (Telegram msg) {
		return stateMachine.handleMessage(msg);
	}

	public void update (float delta) {
		stateMachine.update();
	}
	
	public boolean isSafe() {
		
		return !isThreatened();
	}

	public void attackEnemy() {
		
		Entity target = engine.getSystem(CompetitorSystem.class).getNearestCompetitorInView(transformComponent.newPos, ATTACK_DISTANCE, self);
		
		if (target != null) {
			
			//TransformComponent myPosition = Mappers.transformComponent.get(self);
			TransformComponent enemyPosition = Mappers.transformComponent.get(target);
			
			
			
			pathFindingSkill = new PathFindingSkill(engine, self, target, zoneComponent.zone, zoneComponent.zoneID);
			
			Path path = pathFindingSkill.findPath(MathUtils.floor(transformComponent.newPos.x), MathUtils.floor(transformComponent.newPos.y), MathUtils.floor(enemyPosition.newPos.x), MathUtils.floor(enemyPosition.newPos.y));
			
			if (path != null) {
	/*			for (int p = 0; p<path.getLength();p++) {
					Gdx.app.log("EnemyBrain:attackEnemy"," Path Point: " + path.getX(p) + ", " + path.getY(p));
				}*/
	
				if (path != null && path.getLength() > 1) {
				    int mx = path.getX(1) - MathUtils.floor(transformComponent.newPos.x);
				    int my = path.getY(1) - MathUtils.floor(transformComponent.newPos.y);
			
				    move(mx,my);
				}
				else {
					wander();
				}
				
			}
			
			Gdx.app.log("EnemyBrain:attackEnemy"," I'm going to attack. ");
		}
		
	}
	
	public boolean isThreatened() {
		
		
/*		if (transformComponent != null && zoneComponent != null && Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID) != null) {
			return Mappers.fieldOfViewComponent.get(self).fieldOfView.get(zoneComponent.zoneID).containsEntity(player);
		}*/
		
		Entity target = engine.getSystem(CompetitorSystem.class).getNearestCompetitorInView(transformComponent.newPos, ATTACK_DISTANCE, self);
		
		if (target != null) 
			return true;
		
		/*if (transformComponent != null && target != null && transformComponent.zoneID == Mappers.transformComponent.get(target).zoneID) {
			
			float distance = transformComponent.newPos.dst( Mappers.transformComponent.get(target).newPos);

			if (distance <= 10)
				return true;
		}*/
		return false;
	}

/*	public void headToFinish() {
		
		TransformComponent myPosition = Mappers.transformComponent.get(self);
		Rectangle finish = Mappers.zoneComponent.get(zone).zone.getFinishingArea();

		Path path = pathFindingSkill.findPath(MathUtils.floor(myPosition.newPos.x), MathUtils.floor(myPosition.newPos.y), MathUtils.round(finish.x + (finish.width/2)), MathUtils.round(finish.y + (finish.height/2)));
		
		if (path != null) {
			for (int p = 0; p<path.getLength();p++) {
				Gdx.app.log("EnemyBrain:headToFinish"," Path Point: " + path.getX(p) + ", " + path.getY(p));
			}
			
			for (int x = 0; x < commandComponent.getMaxCommmands();x++) {
				if (path != null && path.getLength() > 1) {
				    int mx = path.getX(1) - MathUtils.floor(myPosition.newPos.x);
				    int my = path.getY(1) - MathUtils.floor(myPosition.newPos.y);
			
				    move(mx,my);
				}
			}
			Gdx.app.log("EnemyBrain:attackEnemy"," I'm racing to the finish. ");
		}		
		else 
			wanderAimlessly();
		
		
	}*/
	
	public void wander(){
	    int mx = MathUtils.random(2) - 1;
	    int my = MathUtils.random(2) - 1;

	    move(mx,my);
	}

	public void move(int mx, int my) {
		
		Direction direction = Direction.getXYDirection(mx, my); 
		
		new MoveCommand(engine, self, direction).execute();
		
		Gdx.app.log("EnemyBrain:move"," I'm going to move " + mx + ", " + my);
		
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
}
