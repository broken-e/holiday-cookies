package com.trey.xmas2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

/** @author trey miller */
public class Bg extends Group {
	public static final int STAR_COUNT = 100;

	private final Xmas2014 app;
	private final Image moon;
	private final Snow snow;
	private final Array<Image> landscapes = new Array<Image>();
	private final Color colBottom = new Color(.17f, .17f, .32f, 1);
	private final Color colTop = new Color(0, 0, .07f, 1);
	private final Array<Image> stars = new Array<Image>(STAR_COUNT);

	// private float wind;

	public Bg(final Xmas2014 app) {
		this.app = app;
		setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// stars
		for (int i = 0; i < STAR_COUNT; i++) {
			Image star = new Image(app.atlas.findRegion("bg/white"));
			star.setPosition(MathUtils.random(Gdx.graphics.getWidth()), MathUtils.random(Gdx.graphics.getHeight()));
			float size = 1 + MathUtils.random(Gdx.graphics.getWidth() * .0042f);
			star.setSize(size, size);
			star.setRotation(MathUtils.random(360));
			float rand = MathUtils.random();
			if (rand < .333f) {// red
				star.setColor(1f, .7f, .74f, .5f);
			} else if (rand < .666f) { // blue
				star.setColor(.74f, .7f, 1, .5f);
			} else { // white or gray
				star.setColor(rand, rand, rand, .5f);
			}
			addActor(star);
			stars.add(star);
		}

		// landscape
		int x = (int) (-Gdx.graphics.getWidth() * .5f);
		while (x < Gdx.graphics.getWidth()) {
			Image landscape = new Image(app.atlas.findRegion("bg/xmas_landscape"));
			landscape.setX(x);
			x += landscape.getWidth();
			landscapes.add(landscape);
			addActor(landscape);
		}

		// moon
		moon = new Image(app.atlas.findRegion("bg/moon"));
		moon.setPosition(30, app.stage.getHeight() - (30 + moon.getHeight()));
		moon.setColor(.7f, .7f, .333f, 1);
		addActor(moon);

		// snow
		snow = new Snow(app.atlas, 21, .9f);
		addActor(snow);
	}

	@Override
	public void act(float delta) {
		if (moon.getX() < getWidth() - moon.getWidth() * 2) { // move moon slowly
			moon.moveBy(delta, 0);
		}
		for (Image star : stars) {
			if (star.getActions().size == 0 && MathUtils.random() < .01f) { // twinkle action
				star.addAction(Actions.sequence(Actions.color(Color.WHITE, .42f), Actions.color(star.getColor(), .2f)));
			}
		}

		super.act(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		// sky gradient
		app.shaper.begin(ShapeType.Filled);
		app.shaper.rect(0, 0, getWidth(), getHeight(), colBottom, colBottom, colTop, colTop);
		app.shaper.end();
		batch.begin();
		super.draw(batch, parentAlpha);
	}

	public void setWind(float wind) {
		snow.setWind(wind);
	}

	public void moveX(float deltaX) {
		float moveAmount = -deltaX * 20;
		Image first = landscapes.first();
		Image last = landscapes.peek();
		if (first.getX() + moveAmount > getX()) {
			last = landscapes.pop();
			last.setX((int) (first.getX() - last.getWidth()));
			landscapes.insert(0, last);
		} else if (last.getX() + last.getWidth() + moveAmount < getX() + getWidth()) {
			first = landscapes.removeIndex(0);
			first.setX((int) (last.getX() + last.getWidth()));
			landscapes.add(first);
		}

		for (Image landscape : landscapes) {
			landscape.moveBy((int) moveAmount, 0);
		}
	}

}
