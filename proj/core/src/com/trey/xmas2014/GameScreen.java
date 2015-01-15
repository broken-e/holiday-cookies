package com.trey.xmas2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trey.xmas2014.MusicManager.MusicSample;
import com.trey.xmas2014.game.AvatarType;
import com.trey.xmas2014.game.Cookie;
import com.trey.xmas2014.game.Game;
import com.trey.xmas2014.game.GameListener;
import com.trey.xmas2014.game.GameTry;

/** @author trey miller */
public class GameScreen extends BaseScreen {

	private final Game game;
	private final Label lblMeters;
	private final Label lblInstructions;
	// emulate keys
	private final Button btnLeft, btnRight;
	private final TextButton btnShowBtns;

	private final HorizontalGroup tblCookies;
	private boolean isPastFirstGameCameraChange = false;

	public GameScreen(final Xmas2014 app) {
		super(app);
		game = new Game(app, AvatarType.Default);

		lblMeters = new Label("0 meters", app.skin.get("xmas-sm", LabelStyle.class));
		lblInstructions = new Label(
				"Collect all "
						+ game.getCookies().getTotalCookies()
						+ " cookies!\n\nTwo ways to play:\nKEYBOARD: Move with arrows and press space to jump.\nShift will make you run.\n\n"
						+ "TOUCH: Open the directional buttons with the button in the lower left.\nTap the right side of the screen to jump.",
				app.skin);
		lblInstructions.setAlignment(Align.top, Align.center);

		tblCookies = new HorizontalGroup();

		Drawable d = app.skin.getDrawable("snowflake");
		float btnSize = Gdx.graphics.getWidth() * .1f;
		d.setMinWidth(btnSize);
		d.setMinHeight(btnSize);

		Drawable dLeft = app.skin.getDrawable("left");
		dLeft.setMinWidth(btnSize);
		dLeft.setMinHeight(btnSize);
		Drawable Right = app.skin.getDrawable("right");
		Right.setMinWidth(btnSize);
		Right.setMinHeight(btnSize);

		btnLeft = new ImageButton(dLeft);
		btnRight = new ImageButton(Right);
		btnLeft.setVisible(false);
		btnRight.setVisible(false);

		Actor jumpTouchBoundsActor = new Actor();
		jumpTouchBoundsActor.setBounds(getWidth() * .5f, getY(), getWidth() * .5f, getHeight());

		btnListener(btnLeft, Keys.LEFT);
		btnListener(btnRight, Keys.RIGHT);
		btnListener(jumpTouchBoundsActor, Keys.SPACE);

		btnShowBtns = new TextButton(">", app.skin.get("xmas-lg", TextButtonStyle.class));
		btnShowBtns.getLabel().setColor(Color.GREEN);
		btnShowBtns.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (btnLeft.isVisible()) {
					btnLeft.setVisible(false);
					btnRight.setVisible(false);
					btnShowBtns.setText(">");
				} else {
					btnLeft.setVisible(true);
					btnRight.setVisible(true);
					btnShowBtns.setText("<");
				}
			}
		});

		Table btnRow = new Table();
		btnRow.add(btnShowBtns);
		btnRow.add(btnLeft);
		btnRow.add(btnRight);
		btnRow.add().expandX().fillX();

		mainTable.add(tblCookies).left().top().expandX().fillX();

		mainTable.add(lblMeters).right().top().expandX();
		mainTable.row();
		mainTable.add(lblInstructions).expand().fill().colspan(2);
		mainTable.row();
		mainTable.add(btnRow).colspan(2).expandX().fillX();

		addActor(jumpTouchBoundsActor);

		game.addListener(new GameListener() {
			float oldCamX, oldCamY;

			@Override
			public void onEvent(GameEvent event, Actor actor) {
				if (event.tag == "CookieManager.gotCookie") {
					TextureRegionDrawable d = new TextureRegionDrawable(((Cookie) event.data).getRegion());
					d.setMinWidth(d.getMinWidth() * .5f);
					d.setMinHeight(d.getMinHeight() * .5f);
					tblCookies.addActor(new Image(d));
				} else if (event.tag == "Game.gameOver") {
					final GameTry gameTry = (GameTry) event.data;
					Gdx.app.postRunnable(new Runnable() {
						public void run() {
							app.player.gameOver(gameTry);
							// game.dispose();
							app.switchScreens(new GameOverScreen(app, gameTry));
						}
					});
				} else if (event.tag == "Game.metersPassedIncrease") {
					int metersPassed = game.getMetersPassed();
					C.sb.setLength(0);
					C.sb.append(metersPassed);
					C.sb.append(" meters");
					lblMeters.setText(C.sb);
					if (metersPassed > 5 && lblInstructions.getParent() != null && lblInstructions.getActions().size == 0) {
						lblInstructions.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeOut(1), Actions.removeActor()));
					}
				} else if (event.tag == "Game.cameraPositionChanged") {
					Vector3 v = ((Game) actor).cam.position;
					if (isPastFirstGameCameraChange) {
						app.gameCameraChanged(v.x, v.y, v.x - oldCamX, v.y - oldCamY);
					}
					oldCamX = v.x;
					oldCamY = v.y;
					isPastFirstGameCameraChange = true;
				}
			}

		});
	}

	/** make btn emulate keys */
	private void btnListener(Actor btn, final int keycode) {
		btn.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				InputAdapter input = game.getInput();
				if (input != null) {
					input.keyDown(keycode);
				}
				Gdx.input.vibrate(1);
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				InputAdapter input = game.getInput();
				if (input != null) {
					input.keyUp(keycode);
				}
			}
		});
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		game.render(); // render game first, then this screen on top
		batch.begin();
		super.draw(batch, parentAlpha);
	}

	@Override
	public void onBackPress() {
		app.switchScreens(new MainScreen((Xmas2014) app));
	}

	@Override
	protected void screenOut() {
		addAction(Actions.fadeOut(.2f));
	}

	@Override
	public BaseScreen show() {
		// ((Xmas2014) app).queueMusic(MusicSample.Theme1, true);
		return super.show();
	}

	@Override
	public void hide() {
		game.dispose();
	}

}
