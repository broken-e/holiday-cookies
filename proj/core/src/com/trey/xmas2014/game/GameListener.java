package com.trey.xmas2014.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Pool;

/**
 * Listen for events with certain tags. Inspired in part by {@link ChangeListener}
 * 
 * @author trey miller
 */
public abstract class GameListener implements EventListener {

	public boolean handle(Event event) {
		if (!(event instanceof GameEvent))
			return false;
		GameEvent e = (GameEvent) event;
		onEvent(e, e.getTarget());
		return false;
	}

	abstract public void onEvent(GameEvent event, Actor actor);

	/** game entities should fire a GameEvent and tag it with the format "EntityClassName.eventName". */
	static public class GameEvent extends Event {
		public String tag;
		public Object data;

		private GameEvent() {
		}

		/**
		 * fire a GameEvent from the actor with the given tag and data.
		 */
		static public void fire(String tag, Actor actor) {
			fire(tag, actor, null);
		}

		/**
		 * fire a GameEvent from the actor with the given tag and data.
		 */
		static public void fire(String tag, Actor actor, Object data) {
			GameEvent e = pool.obtain();
			e.tag = tag;
			e.data = data;
			actor.fire(e);
			e.tag = null;
			e.data = null;
			pool.free(e);
		}
	}

	private static final Pool<GameEvent> pool = new Pool<GameEvent>() {
		@Override
		protected GameEvent newObject() {
			return new GameEvent();
		}
	};
}
