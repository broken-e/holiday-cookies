package com.trey.xmas2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

/**
 * draws the background behind/between the floor and ceiling. Adopted from an earlier project.
 * 
 * @author trey miller
 */
public class Snow extends Group {

	private boolean updateWithTime = true;
	private float scaleMod = 1f;
	private float baseSizeFactor = .042f; // multiplied by getWidth() to get the base size of flake
	private float minRandomSizeFactor = .8f; // min adjust to base size
	private float maxRandomSizeFactor = 1.1f; // max adjust to base size
	private float minAbsoluteSize = .25f;
	private float rate = 4f; // seconds between new flakes
	private int maxFlakes = 42;// max flakes

	private float wind = .15f; // mods x to move flakes faster
	private float fallSpeed = .2f; // mods y to move flakes faster or slower
	private float rateAccum = 0f;

	private final Array<SpriteActor> flakes = new Array<SpriteActor>();
	private final Array<AtlasRegion> regions;

	public Snow(TextureAtlas atlas) {
		regions = atlas.findRegions("snowflake");
		setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		setTouchable(Touchable.disabled);
		newFlake(); // start with one
	}

	public Snow(TextureAtlas atlas, int maxFlakes, float scaleMod) {
		this(atlas);
		this.maxFlakes = maxFlakes;
		this.scaleMod = scaleMod;
	}

	public void setWind(float wind) {
		this.wind = wind;
	}

	@Override
	public void act(float delta) {
		if (getStage() != null) {
			float diffX = delta * getWidth() * wind;
			float diffY = -delta * getHeight() * fallSpeed;
			iterOverFlakes(diffX, diffY);
			if (updateWithTime && flakes.size < maxFlakes) {
				rateAccum += delta;
				if (rateAccum >= rate) {
					rateAccum = 0;
					newFlake();
				}
			}
		}
		super.act(delta);
	}

	private void newFlake() {
		SpriteActor flake = new SpriteActor(regions.random());
		float flakeSize = getWidth() * baseSizeFactor * MathUtils.random(minRandomSizeFactor, maxRandomSizeFactor);
		flake.setSize(flakeSize, flakeSize);
		flake.setOrigin(MathUtils.random(flake.getWidth()), MathUtils.random(flake.getHeight()));
		flake.addAction(Actions.forever(Actions.rotateBy(MathUtils.random(-360f, 360f), 1f)));
		// if (MathUtils.random() > .42f) {
		float time = MathUtils.random(1, 7);
		float squish = MathUtils.random(flakeSize);
		flake.addAction(Actions.forever(Actions.sequence(Actions.sizeTo(squish, flakeSize, time),
				Actions.sizeTo(flakeSize, flakeSize, time))));
		// }
		flakes.add(flake);
	}

	/** input the amount of position change the flake should make */
	private void iterOverFlakes(float diffX, float diffY) {
		for (int i = 0; i < flakes.size; i++) {
			SpriteActor flake = flakes.get(i);
			if (flake.getParent() == null) { // reset flake position
				float x, y;
				if (MathUtils.random() > .7f) { // start at side
					x = wind < 0 ? getX() + getWidth() : getX() - flake.getWidth();
					y = MathUtils.random() * getHeight();
				} else { // start at top
					x = MathUtils.random() * getWidth();
					y = getY() + getHeight();
				}
				flake.setPosition(x, y);
				addActor(flake);
			} else {
				if (flake.getX() + Math.abs(flake.getWidth()) < getX() || flake.getY() + Math.abs(flake.getHeight()) < getY()) {
					flake.remove();
				}
			}
			float factor = ((float) i / (float) maxFlakes) * scaleMod;
			factor = Math.max(minAbsoluteSize, factor);
			flake.setScale(factor);
			flake.moveBy(diffX * factor, diffY * factor);
		}
	}

}
