package com.trey.xmas2014.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.trey.xmas2014.SpriteActor;

/** @author trey miller */
public class Platform extends SpriteActor {

	private final Game game;
	private static final Color blueColor = new Color(.95f, .95f, 1f, 1f);

	private float delayMin = 1f, delayMax = 5f;

	public Platform(Game game) {
		super(game.atlas.findRegion("game/platform_snow"));
		this.game = game;
		setColor(blueColor);
		addAction(shineAction());
	}

	private Action shineAction() {
		return Actions.sequence(Actions.delay(MathUtils.random(delayMin, delayMax)), Actions.color(Color.WHITE, .1f),
				Actions.color(blueColor, .1f), Actions.run(addAction));
	}

	private Runnable addAction = new Runnable() {
		public void run() {
			addAction(shineAction());
		}
	};
}
