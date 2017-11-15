package com.lofisoftware.robotarena.systems;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.utils.Array;
import com.lofisoftware.robotarena.components.TurnComponent;
import com.lofisoftware.robotarena.components.TurnComponent.TURN;
import com.lofisoftware.robotarena.messages.Messages;
import com.lofisoftware.robotarena.messages.TurnCompleteMessage;

public class TurnSystem extends IteratingSystem implements Telegraph {
	
	private Array<Entity> turnQueue;
	private Comparator<Entity> comparator;
	boolean turnComplete, gameOver;
	int turns, nextTurn, totalTurns;
	private ImmutableArray<Entity> turnEntities;
	//private Entity [] turnLine;
	
	Entity entityPlaying;
	int entityPlayingPriority;
	
	//PerformanceCounter counter;
	
	@SuppressWarnings("unchecked")
	public TurnSystem() {
		super(Family.getFor(TurnComponent.class));
		
		//counter = new PerformanceCounter("Turn System");
		
		turnQueue = new Array<Entity>();
		turns = nextTurn = totalTurns = 0;
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				return (int)Math.signum(Mappers.turnComponent.get(entityA).priority -
						Mappers.turnComponent.get(entityB).priority);
			}
		};
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {

	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		//counter.tick(deltaTime);
		//counter.start();
		
		if (gameOver)
			return;
		
		if (totalTurns != turnEntities.size()) {
			//Gdx.app.log("TurnSystem:update","Turns !- turnentity size");
			determineTurns();
			totalTurns = turnEntities.size();
			
			if (turnQueue != null && !turnQueue.contains(entityPlaying, true)) {
				// the entity playing is no longer in queue
				//Gdx.app.log("TurnSystem:update","Entity playing is not turn queue ");
				startNextInLine();
				//counter.stop();
				return;
			}
		}
		
		if (!Mappers.turnComponent.has(entityPlaying)) {
			//Gdx.app.log("TurnSystem:update","entityPlaying is missing ");
			determineTurns();
			startNextInLine();
			//counter.stop();
			return;
		}
		
		if (turnComplete) {
			//Gdx.app.log("TurnSystem:update","Turn complete ");
			startNextInLine();
		}
		
		//counter.stop();
		//MessageDispatcher.getInstance().dispatchMessage(null, Messages.TURN_COMPLETE);
		
	}

	private void startNextInLine() {
		

		
		for (Entity e : turnQueue) {
			
			TurnComponent turnComponent = Mappers.turnComponent.get(e);
			if (turnComponent.active && turnComponent.priority > entityPlayingPriority) {
				turnComplete = false;
				entityPlaying = e;
				entityPlayingPriority = turnComponent.priority;
				turnComponent.state = TURN.GO;
				turnComponent.turn++;
				Gdx.app.log("TurnSystem:startNextInLine","Turn: " + Mappers.characterComponent.get(e).getName());
				return;
			}
		}
		
		entityPlayingPriority = -1;
		turns++;
		Gdx.app.log("TurnSystem:startNextInLine","Turn " + turns + " completed.");
		MessageDispatcher.getInstance().dispatchMessage(null, Messages.ROUND_COMPLETE);
		//Gdx.app.log("TurnSystem:update", "Counter load: " + counter.toString());
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Messages.TURN_COMPLETE) {
			
			if(msg.extraInfo != null && TurnCompleteMessage.class.isAssignableFrom(msg.extraInfo.getClass()) ) {
				Entity entity = ((TurnCompleteMessage)msg.extraInfo).entity;
				
				if (entityPlaying.getId() != entity.getId())
					Gdx.app.error("TurnSystem:handleMessage", "Entity went out of turn!" + entity.getId());
				
				Mappers.turnComponent.get(entity).state = TURN.WAITING;
				
				turnComplete = true;
				
			}

			return true;
		}
		if (msg.message == Messages.GAME_OVER) {
			gameOver = true;
		}
		return false;
	}
	
	public void determineTurns() {
		
		turnQueue.clear();
		for (int x = 0; x < turnEntities.size(); x++) {
			turnQueue.add(turnEntities.get(x));
		}
		turnQueue.sort(comparator);
	}
	
	public int getNextTurnNumber() {
		nextTurn++;
		return nextTurn;
	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		turnEntities = engine.getEntitiesFor(Family.getFor(TurnComponent.class));
		

	}
}
