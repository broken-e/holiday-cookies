package com.trey.xmas2014.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

/** @author trey miller */
public class PlatformManager extends Group {

	private final Game game;
	public static final float PLATFORM_HEIGHT = .5f;
	private float minGap = 1f;
	private float maxGap = 4f;
	private float minHeightGap = -3f;
	private float maxHeightGap = 3f;
	private float minWidth = 1f;
	private float maxWidth = 3f;

	// consider changing to a tree sorted by x, assuming platform.x doesn't change
	private Array<Platform> platforms = new Array<Platform>();

	/** temp rects */
	private final Rectangle rect = new Rectangle(), rect2 = new Rectangle();

	public PlatformManager(Game game) {
		this.game = game;
		Platform firstPlatform = new Platform(game);
		Rectangle viewportBounds = game.getViewportBounds();
		firstPlatform.setBounds(viewportBounds.x, viewportBounds.y, viewportBounds.width * .5f, PLATFORM_HEIGHT);
		platforms.add(firstPlatform);
		addActor(firstPlatform);
		setPlatform(new Platform(game), firstPlatform);

		game.addListener(new GameListener() {
			@Override
			public void onEvent(GameEvent event, Actor actor) {
				if (event.tag == "Game.cameraPositionChanged") {
					checkPlatforms();
				}
			}
		});
	}

	/** checks if a platform needs to be placed at the end */
	private void checkPlatforms() {
		Platform last = platforms.peek();
		Rectangle viewportBounds = game.getViewportBounds();
		if (last.getX() < viewportBounds.x + viewportBounds.width) { // last platform exposed. need to stack more past it
			setPlatform(new Platform(game), last);
		}
	}

	/** sets the next platform in a random spot relative to the previous platform */
	private void setPlatform(Platform platform, Platform previous) {
		Rectangle viewportBounds = game.getViewportBounds();
		float x = previous == null ? viewportBounds.x : previous.getX() + previous.getWidth();
		x += MathUtils.random(minGap, maxGap);

		float y = previous == null ? viewportBounds.y : previous.getY() + previous.getHeight();
		y += MathUtils.random(minHeightGap, maxHeightGap);
		y = Math.max(0, y);

		float width = MathUtils.random(minWidth, maxWidth);
		platform.setBounds(x, y, width, PLATFORM_HEIGHT);
		if (platform.getParent() == null) {
			addActor(platform);
		}
		platforms.add(platform);
	}

	/**
	 * returns the first rectangle found to overlap the actor's bounds, or null if none overlap. do not keep the rectangle instance.
	 * If this were a larger scale game, I'd worry about optimizing.
	 * */
	public Rectangle collides(Actor a) {
		rect.set(a.getX(), a.getY(), a.getWidth(), a.getHeight());
		for (Platform p : platforms) {
			rect2.set(p.getX(), p.getY(), p.getWidth(), p.getHeight());
			if (rect.overlaps(rect2)) {
				return rect2;
			}
		}
		return null;
	}

	/** returns a Rectangle representing the bounds of what should be shown. Not too cheap. */
	public Rectangle getCurrentViewBounds() {
		Rectangle r = game.getViewportBounds();
		float minY = Float.MAX_VALUE, maxY = Float.MIN_VALUE;
		for (int i = 0; i < platforms.size; i++) {
			Platform p = platforms.get(i);
			if (p.getRight() >= r.x && p.getX() <= r.x + r.width) {
				minY = Math.min(minY, p.getY());
				maxY = Math.max(maxY, p.getTop());
			}
		}
		if (minY == Float.MAX_VALUE) {
			minY = 0;
		}
		if (maxY == Float.MIN_VALUE) {
			maxY = 0;
		}
		r.y = minY;
		r.height = maxY - minY;
		return r;
	}

	/** returns nearest platform on the x axis past the given x value, by going either the right or left direction */
	public Platform getFirstPlatformFromX(float x, boolean toRight) {
		if (toRight) {
			for (Platform platform : platforms) {
				if (platform.getX() >= x) {
					return platform;
				}
			}
		} else {
			for (int i = platforms.size - 1; i >= 0; i--) {
				Platform platform = platforms.get(i);
				if (platform.getX() + platform.getWidth() <= x) {
					return platform;
				}
			}
		}
		return null;
	}
}
