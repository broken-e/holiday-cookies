package com.trey.xmas2014.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.trey.xmas2014.UiApp;
import com.trey.xmas2014.Xmas2014;
import com.trey.xmas2014.game.GameListener.GameEvent;

/**
 * the main actor that controls the whole game
 * 
 * @author trey miller
 */
public class Game extends Group implements Disposable {
	public static final float WORLD_HEIGHT = 9;

	private final Xmas2014 app;
	public final TextureAtlas atlas;

	private final Stage stage;
	public final Camera cam;
	private final CameraActor camActor;

	private final Avatar avatar;
	private final PlatformManager platforms;
	private final CookieManager cookies;

	private int metersPassed = 0;
	private int leftPressed = 0, rightPressed = 0;

	private final Rectangle viewportBounds = new Rectangle();

	private InputAdapter input;

	private final GameTry gameTry;

	public Game(final Xmas2014 app, AvatarType avatarType) {
		this.app = app;
		this.atlas = app.atlas;
		stage = new Stage(new ExtendViewport(WORLD_HEIGHT, WORLD_HEIGHT));
		cam = stage.getViewport().getCamera();
		camActor = new CameraActor(cam);
		camActor.setUpdateMode(false, true, true, true);

		setBounds(cam.position.x - cam.viewportWidth * .5f, cam.position.y - cam.viewportHeight * .5f, cam.viewportWidth,
				cam.viewportHeight);
		viewportBounds.setSize(cam.viewportWidth, cam.viewportHeight);
		viewportBounds.setCenter(cam.position.x, cam.position.y);

		avatar = new Avatar(this, avatarType);

		avatar.setSize(1.5f, 3f);
		addListener(new GameListener() {
			@Override
			public void onEvent(GameEvent event, Actor actor) {
				if (event.tag == "Avatar.onMove") {
					Rectangle rect = platforms.collides(avatar);
					if (rect != null) {
						avatar.onCollide(rect);
					}
				} else if (event.tag == "Avatar.onDeath") {
					gameOver();
				} else if (event.tag == "CookieManager.gotCookie") {
					if (!cookies.isCookieLeft()) {
						Gdx.app.postRunnable(new Runnable() {
							public void run() {
								gameOver();
							}
						});
					}
				}
			}
		});

		platforms = new PlatformManager(this);

		cookies = new CookieManager(this);

		gameTry = new GameTry(this);
		gameTry.cookiesTotal = cookies.getTotalCookies();

		addActor(platforms);
		addActor(avatar);
		addActor(cookies);
		addActor(camActor);

		stage.addActor(this);
		setInputs();
	}

	private void onDirectionChange() {
		int direction = leftPressed + rightPressed;
		avatar.setDirection(direction);
		app.setWind(-direction - .1f);
	}

	/** called from GameScreen. This is the whole game loop basically */
	public void render() {
		stage.act();
		Camera cam = stage.getViewport().getCamera();
		float oldCamX = cam.position.x;
		float oldCamY = cam.position.y;

		cam.position.x = avatar.getX();

		Rectangle r = platforms.getCurrentViewBounds();
		if ((r.y != cam.position.y && camActor.getActions().size == 0)) {
			float newY = r.y + cam.viewportHeight * .5f;
			camActor.addAction(Actions.moveTo(cam.position.x, newY, .42f));
		}
		viewportBounds.setSize(cam.viewportWidth, cam.viewportHeight);
		viewportBounds.setCenter(cam.position.x, cam.position.y);

		if (metersPassed < (int) avatar.getX()) {
			metersPassed = (int) avatar.getX();
			GameEvent.fire("Game.metersPassedIncrease", this);
		}
		if (oldCamX != cam.position.x || oldCamY != cam.position.y) {
			GameEvent.fire("Game.cameraPositionChanged", this);
		}
		stage.draw();
	}

	public PlatformManager getPlatforms() {
		return platforms;
	}

	public CookieManager getCookies() {
		return cookies;
	}

	public int getMetersPassed() {
		return metersPassed;
	}

	public Rectangle getViewportBounds() {
		return viewportBounds;
	}

	/** the y value where the avatar dies */
	public float getDeathY() {
		return 0;
	}

	private void gameOver() {
		GameEvent.fire("Game.gameOver", this, gameTry);
		addAction(Actions.fadeOut(.5f));
	}

	@Override
	public void dispose() {
		UiApp.inputs.removeProcessor(input);
		input = null;
		stage.dispose();
	}

	private void setInputs() {
		// TODO override touch methods for mobile
		input = new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.LEFT || keycode == Keys.A) {
					leftPressed = -1;
				} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
					rightPressed = 1;
				} else if (keycode == Keys.UP || keycode == Keys.SPACE || keycode == Keys.W) {
					avatar.startJumping();
				} else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
					avatar.startRunning();
				} else {
					return false;
				}
				onDirectionChange();
				return true;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Keys.LEFT || keycode == Keys.A) {
					leftPressed = 0;
				} else if (keycode == Keys.RIGHT || keycode == Keys.D) {
					rightPressed = 0;
				} else if (keycode == Keys.SPACE || keycode == Keys.W) {
					avatar.stopJumping();
				} else if (keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
					avatar.stopRunning();
				} else {
					return false;
				}
				onDirectionChange();
				return true;
			}
		};
		UiApp.inputs.addProcessor(input);
	}

	public InputAdapter getInput() {
		return input;
	}

}
