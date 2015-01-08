package com.trey.xmas2014;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trey.xmas2014.MusicManager.MusicSample;
import com.trey.xmas2014.game.GameTry;

/** @author trey miller */
public class GameOverScreen extends BaseScreen {

	public GameOverScreen(final Xmas2014 app, final GameTry gameTry) {
		super(app);
		String text = "";
		if (gameTry.cookiesGot == gameTry.cookiesTotal) {
			text = "You got all the cookies!\nMerry Holidays to you!";
		} else {
			text = "Game Over!\nYou got " + gameTry.cookiesGot + " out of " + gameTry.cookiesTotal + " cookies.";
		}

		Label lbl = new Label(text, app.skin.get("xmas-sm", LabelStyle.class));
		lbl.setAlignment(Align.center);
		mainTable.add(lbl);
		addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				done();
			}

			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.SPACE) {
					done();
				}
				return false;
			}
		});
		setTouchable(Touchable.enabled);
		for (AtlasRegion cookie : gameTry.cookies) {
			Image img = new Image(cookie) {
				@Override
				public void act(float delta) {
					if (getY() + getHeight() < GameOverScreen.this.getY()) {
						setY(GameOverScreen.this.getY() + GameOverScreen.this.getHeight() * 1.3f);
						setX(MathUtils.random(GameOverScreen.this.getWidth()));
						clearActions();
						addAction(Actions.forever(Actions.rotateBy(MathUtils.random(-42, 42f), .1f)));
						addAction(Actions.forever(Actions.moveBy(MathUtils.random(-10, 10f), -MathUtils.random(10, 30f), .1f)));
					}
					super.act(delta);
				}
			};
			img.setOrigin(Align.center);
			img.setY(-42000);
			addActor(img);
		}
		app.queueMusic(MusicSample.GameOver, true);
	}

	@Override
	public BaseScreen show() {
		getColor().a = 0;
		addAction(Actions.fadeIn(app.defaultTransitionTime));
		return this;
	}

	private void done() {
		app.switchScreens(new MainScreen((Xmas2014) app));
		((Xmas2014)app).fadeMusic();
	}

	@Override
	public void onBackPress() {
		app.switchScreens(new MainScreen((Xmas2014) app));
	}

}
