package com.trey.xmas2014;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.trey.xmas2014.MusicManager.MusicSample;

/** @author trey miller */
public class MainScreen extends BaseScreen {

	public MainScreen(final Xmas2014 app) {
		super(app);
		final Label lblTitle = new Label("Holiday\nCookies\n2014", app.skin.get("xmas-lg", LabelStyle.class)) {
			float xbase = Gdx.graphics.getWidth() * .1f;
			float xdir = xbase;
			float ybase = Gdx.graphics.getHeight() * .1f;
			float ydir = ybase;

			@Override
			public void act(float delta) {
				super.act(delta);
				float x = getX(), y = getY();
				setX(x + xdir * delta);
				if (x < 0) {
					xdir = xbase;
				} else if (x + getWidth() > Gdx.graphics.getWidth()) {
					xdir = -xbase;
				}
				setY(y + ydir * delta);
				if (y < 0) {
					ydir = ybase;
				} else if (y + getHeight() > Gdx.graphics.getHeight()) {
					ydir = -ybase;
				}
			}
		};
		lblTitle.setAlignment(Align.center);
		lblTitle.setPosition(42, Gdx.graphics.getHeight() - lblTitle.getHeight());

		Label lblInstructions = new Label("(Click To Begin)", app.skin.get("default", LabelStyle.class));
		lblInstructions.setColor(1, 1, 1, 0);
		lblInstructions.addAction(Actions.sequence(Actions.delay(10), Actions.fadeIn(1)));

		Label lblToFrom = new Label("To: My Family\nFrom: Trey", app.skin.get("xmas-sm", LabelStyle.class));
		// lblToFrom.setColor(0, 1, 0, 1);
		mainTable.row().uniform();
		mainTable.add(lblToFrom).left();
		mainTable.add(lblInstructions).expand().fillY().center();
		mainTable.add().right();
		// mainTable.row();

		mainTable.addActor(lblTitle);
		lblTitle.addAction(Actions.forever(Actions.sequence(Actions.color(Color.RED, .666f), Actions.color(Color.GREEN, .666f))));

		this.setTouchable(Touchable.enabled);
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
		app.forceMusicChange();
		app.queueMusic(MusicSample.Intro, true);
	}

	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	@Override
	public void onBackPress() {
		Gdx.app.exit();
	}

	private void done() {
		// app.switchScreens(new SelectCharacterScreen((Xmas2014) app));
		app.switchScreens(new GameScreen((Xmas2014) app));
		((Xmas2014) app).fadeMusic();
	}
}
