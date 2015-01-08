package com.trey.xmas2014.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.trey.xmas2014.game.GameListener.GameEvent;

/** @author trey miller */
public class CookieManager extends Group {

	public static final float COOKIE_SIZE = 1.5f;
	private int metersBetweenCookies = 17;

	private final int totalCookies;
	private int cookiesGot = 0;
	private final Array<AtlasRegion> cookiesLeft;
	private final Array<Cookie> cookiesInPlay = new Array<Cookie>();
	/** the meters that triggered a cookie, so that it doesn't happen twice */
	private final IntArray metersTriggered = new IntArray();

	private final Rectangle rect = new Rectangle(), rect2 = new Rectangle();
	private final Game game;

	public CookieManager(final Game game) {
		this.game = game;
		cookiesLeft = game.atlas.findRegions("game/cookie");
		totalCookies = cookiesLeft.size;
		cookiesLeft.reverse();
		metersTriggered.add(0);
		game.addListener(new GameListener() {

			private float prevX = 0, prevY = 0;

			@Override
			public void onEvent(GameEvent event, Actor actor) {
				if (event.tag == "Avatar.onMove") {
					if (actor.getX() != prevX || actor.getY() != prevY) {
						rect.set(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
						for (Cookie cookie : cookiesInPlay) {
							if (rect.overlaps(rect2.set(cookie.getX(), cookie.getY(), cookie.getWidth(), cookie.getHeight()))) {
								CookieManager.this.gotCookie(cookie);
								break;
							}
						}
						prevX = actor.getX();
						prevY = actor.getY();
					}
				} else if (event.tag == "Game.metersPassedIncrease") {
					int meter = game.getMetersPassed();
					if (meter % metersBetweenCookies == 0 && !metersTriggered.contains(meter)) {
						nextCookie();
						metersTriggered.add(meter);
					}
				}
			}

		});
	}

	public int getTotalCookies() {
		return totalCookies;
	}

	/** the number of cookies attained by the avatar in this game */
	public int getCookiesGot() {
		return cookiesGot;
	}

	public void gotCookie(Cookie cookie) {
		cookiesGot++;
		cookiesInPlay.removeValue(cookie, true);

		removeActor(cookie);
		GameEvent.fire("CookieManager.gotCookie", this, cookie);
	}

	public boolean isCookieLeft() {
		return (cookiesInPlay.size != 0 || cookiesLeft.size != 0);
	}

	public void nextCookie() {
		if (cookiesLeft.size == 0) {
			return;
		}
		Rectangle vp = game.getViewportBounds();
		Platform platform = game.getPlatforms().getFirstPlatformFromX(vp.x + vp.width, true);
		if (platform != null) {
			Cookie cookie = new Cookie(cookiesLeft.pop());
			cookiesInPlay.add(cookie);
			float x = platform.getX() + platform.getWidth() * .5f - COOKIE_SIZE * .5f;
			float y = platform.getY() + 1.5f;
			cookie.setBounds(x, y, COOKIE_SIZE, COOKIE_SIZE);
			addActor(cookie);
		}

	}
}
