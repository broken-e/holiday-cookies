package com.trey.xmas2014;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.trey.xmas2014.game.GameTry;

/**
 * class holding all player info
 * 
 * @author trey miller
 * */
public class Player extends Actor {

	private final Xmas2014 app;
	private final Array<GameTry> gameTries = new Array<GameTry>();

	public Player(Xmas2014 app) {
		this.app = app;
	}

	public void gameOver(GameTry gameTry) {
		gameTries.add(gameTry);
	}
}
