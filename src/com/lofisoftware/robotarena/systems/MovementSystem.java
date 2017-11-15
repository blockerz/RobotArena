package com.lofisoftware.robotarena.systems;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.RobotArena;
import com.lofisoftware.robotarena.components.MovementComponent;
import com.lofisoftware.robotarena.components.StateComponent;
import com.lofisoftware.robotarena.components.StateComponent.STATE;
import com.lofisoftware.robotarena.components.TransformComponent;
import com.lofisoftware.robotarena.messages.EntityMovedMessage;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.tween.VectorAccessor;


public class MovementSystem extends IteratingSystem {
	
	private class MovementTweenCallback implements TweenCallback { 
		
		private Entity entity;
		
		public MovementTweenCallback(Entity entity){
			this.entity = entity;
		}
		
        public void onEvent(int type, BaseTween<?> source) {
        	if (entity != null) {
        		try {
        			Mappers.stateComponent.get(entity).set(STATE.READY);
        			Mappers.transformComponent.get(entity).moving = false;
        			MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_MOVED, new EntityMovedMessage(entity));	
        		}
        		catch (NullPointerException e) {}
        	}
        }
    };
    
	@SuppressWarnings("unchecked")
	public MovementSystem() {
		super(Family.getFor(TransformComponent.class, MovementComponent.class, StateComponent.class));

	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = Mappers.transformComponent.get(entity);
		MovementComponent mov = Mappers.movementComponent.get(entity);
		StateComponent state = Mappers.stateComponent.get(entity);
		
		if (state.get() == STATE.MAP_COLLISION || state.get() == STATE.ENTITY_COLLISION) {
			
			state.set(STATE.MOVING);
			pos.moving = true;
			
			Vector3 bounceTo = new Vector3(pos.newPos.x + mov.move.x /2 ,pos.newPos.y + mov.move.y /4,0);
			//Gdx.app.log("CollisionSystem:"," bounceTo: " + bounceTo.toString());
			if (mov.animate) {
				Timeline.createSequence().push(Tween.to(pos.pos, VectorAccessor.POS_XY, mov.animationTime/4)
						.target(bounceTo.x,bounceTo.y).ease(TweenEquations.easeOutQuint))
						.push(Tween.to(pos.pos, VectorAccessor.POS_XY, mov.animationTime/2)
						.target(pos.newPos.x,pos.newPos.y).ease(TweenEquations.easeOutQuint))
						.setCallback(new MovementTweenCallback(entity))
						.start(RobotArena.getTweenManager());
			}
			else {
				state.set(STATE.READY);
				pos.moving = false;
				MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_MOVED, new EntityMovedMessage(entity));	
			}
			//Gdx.app.log("CollisionSystem:"," pos: " + pos.pos.toString());
			//Gdx.app.log("CollisionSystem:"," newPos: " + pos.newPos.toString());
		}
		else {
			pos.newPos.add(mov.move);
			
			state.set(STATE.MOVING);
			pos.moving = true;
			
			if (mov.animate) {
				
				Timeline.createSequence().push(Tween.to(pos.pos, VectorAccessor.POS_XY, mov.animationTime)
						.target(pos.newPos.x,pos.newPos.y).ease(TweenEquations.easeOutQuint)
						.setCallback(new MovementTweenCallback(entity))
						.start(RobotArena.getTweenManager()));
			}
			else {
				pos.pos.set(pos.newPos);
				state.set(STATE.READY);
				pos.moving = false;
				MessageDispatcher.getInstance().dispatchMessage(null, Messages.ENTITY_MOVED, new EntityMovedMessage(entity));	
			}
		}				
		entity.remove(MovementComponent.class);
	}
}
