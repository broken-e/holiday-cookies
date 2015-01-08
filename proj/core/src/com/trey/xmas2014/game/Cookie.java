package com.trey.xmas2014.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.trey.xmas2014.SpriteActor;

/** @author trey miller */
public class Cookie extends SpriteActor {
	private final float rotationAmount = 42, rotationSpeed = 1.234f;

	public Cookie(AtlasRegion region) {
		super(region);

		setRotation(rotationAmount);
		addAction(Actions.forever(Actions.sequence(Actions.rotateTo(-rotationAmount, rotationSpeed),
				Actions.rotateTo(rotationAmount, rotationSpeed))));
	}

	@Override
	protected void sizeChanged() {
		setOrigin(getWidth() * .5f, getHeight() * .5f);
	}

}