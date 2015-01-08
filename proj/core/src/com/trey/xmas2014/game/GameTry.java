package com.trey.xmas2014.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * holds details about one try in the game, and functions for determining if won or not.
 * 
 * @author trey miller
 */
public class GameTry {

	public String name;
	public float meters;
	public int cookiesGot, cookiesTotal;
	public final Array<AtlasRegion> cookies = new Array<AtlasRegion>();

	public GameTry(final Game game) {
		game.addListener(new GameListener() {
			@Override
			public void onEvent(GameEvent event, Actor actor) {
				if (event.tag == "CookieManager.gotCookie") {
					cookies.add(new AtlasRegion(((Cookie) event.data).getRegion()));
					cookiesGot = ((CookieManager) actor).getCookiesGot();
				} else if (event.tag == "Game.metersPassedIncrease") {
					meters = game.getMetersPassed();
				}
			}
		});
	}
}
