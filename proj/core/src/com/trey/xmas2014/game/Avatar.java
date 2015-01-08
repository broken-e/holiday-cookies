package com.trey.xmas2014.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pools;
import com.trey.xmas2014.game.GameListener.GameEvent;

/**
 * this represents the player in the game
 * 
 * @author trey miller
 */
public class Avatar extends Actor {

	// adjustable variables
	private float baseSpeed = 7f;
	private float runSpeed = 14f;
	private float gravity = 17f;
	private float maxJumpTime = .333f;
	private float jumpPower = 7f;
	private float accelTime = .42f; // amount of time it takes from standing to full speed

	// state variables
	private float xVel = 0;
	private float yVel = 0;
	private float airTime = 0; // amount of time spend not on the ground
	private float jumpTime = -1; // if < 0, we are not jumping
	private float stateTime = 0;
	private boolean isFacingRight = false;
	private boolean isRunning = false;
	private boolean dead = false;
	private Rectangle collidingBounds;

	private State state;
	private State prevState;

	private AvatarType type = AvatarType.Default;

	private final IdentityMap<TextureRegion, AtlasSprite> animSprites = new IdentityMap<TextureRegion, AtlasSprite>();
	private final Animation walkAnim, standAnim, jumpAnim;

	private Sound jumpSound, eatSound;

	private Game game;

	public enum State {
		Standing, Walking, Jumping,
	}

	public Avatar(Game game, AvatarType type) {
		this.game = game;
		this.type = type;
		walkAnim = new Animation(.17f, game.atlas.findRegions(type.walkRegions), PlayMode.LOOP_PINGPONG);
		standAnim = new Animation(1, game.atlas.findRegions(type.standRegions));
		jumpAnim = new Animation(.4f, game.atlas.findRegions(type.jumpRegions));
		addAnimSprites(walkAnim);
		addAnimSprites(standAnim);
		addAnimSprites(jumpAnim);

		jumpSound = Gdx.audio.newSound(Gdx.files.internal("jump-sound.ogg"));
		eatSound = Gdx.audio.newSound(Gdx.files.internal("eating-sound.ogg"));

		state = prevState = State.Standing;
		setY(PlatformManager.PLATFORM_HEIGHT);
		
		game.addListener(new GameListener() {
			@Override
			public void onEvent(GameEvent event, Actor actor) {
				if (event.tag == "CookieManager.gotCookie") {
					eatSound.play();
				}
			}
			
		});
	}

	private void addAnimSprites(Animation anim) {
		TextureRegion[] keys = anim.getKeyFrames();
		for (int i = 0; i < keys.length; i++) {
			animSprites.put(keys[i], new AtlasSprite((AtlasRegion) keys[i]));
		}
	}

	@Override
	public void act(float delta) {
		if (collidingBounds != null && !isStandingOn(collidingBounds)) {
			Pools.free(collidingBounds);
			collidingBounds = null;
			airTime = 0;
		}
		stateTime += delta;
		stateCheck();
		float xDelta = 0, yDelta = 0;
		if (xVel != 0) { // set amount to move x
			xDelta = xVel * (isRunning ? runSpeed : baseSpeed) * delta;
			if (state == State.Walking && prevState == State.Standing && stateTime < accelTime) {
				xDelta *= stateTime / accelTime; // accelerate up to speed
			}
		}
		if (isOnGround()) { // make sure not falling
			airTime = 0;
			if (yVel < 0) {
				yVel = 0;
			}
		} else { // subtract gravity pull
			airTime += delta;
			yVel -= gravity * airTime * airTime;
		}
		if (jumpTime >= maxJumpTime) { // end jumping
			stopJumping();
		}
		if (jumpTime >= 0) { // calculate and add jumpMod
			jumpTime += delta;
			float jumpMod = jumpPower * (maxJumpTime - jumpTime);
			if (jumpMod > 0) {
				yVel += jumpMod;
			}
		}
		yDelta = yVel * delta;
		if (xDelta != 0 || yDelta != 0) {
			moveBy(xDelta, yDelta);
			GameEvent.fire("Avatar.onMove", this);
		}
		if (!dead && getY() < game.getDeathY() - getHeight() * 2) { // check death
			died();
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		float x = getX();
		float width = getWidth();
		if (isFacingRight) { // draw flipped
			width = -width;
			x -= width;
		}
		AtlasSprite sprite = animSprites.get(getAnim(state).getKeyFrame(stateTime));
		sprite.setBounds(x, getY(), width, getHeight());
		sprite.setOrigin(getWidth() * .5f, getHeight() * .5f);
		sprite.draw(batch);
	}

	/** checks current state and changes it if necessary */
	private void stateCheck() {
		prevState = state;
		if (jumpTime >= 0 || airTime > 0) {
			state = State.Jumping;
		} else if (xVel == 0) {
			state = State.Standing;
		} else {
			state = State.Walking;
		}
		if (prevState != state) {
			stateTime = 0;
		}
	}

	private Animation getAnim(State state) {
		switch (state) {
		case Jumping:
			return jumpAnim;
		case Walking:
			return walkAnim;
		case Standing:
		default:
			return standAnim;
		}
	}

	/** -1 for moving left, 1 for moving right, 0 for stop */
	public void setDirection(int direction) {
		if (direction > 0) {
			isFacingRight = true;
		} else if (direction < 0) {
			isFacingRight = false;
		}
		xVel = direction;
	}

	public void startRunning() {
		isRunning = true;
	}

	public void stopRunning() {
		isRunning = false;
	}

	public void startJumping() {
		if (isOnGround()) {
			jumpTime = 0;
			jumpSound.play();
		}
	}

	public void stopJumping() {
		jumpTime = -1;
	}

	public boolean isJumping() {
		return jumpTime >= 0;
	}

	/** called by game when collision is going on */
	public void onCollide(Rectangle bounds) {
		float leeway = -yVel * Gdx.graphics.getDeltaTime() + .01f;// getHeight() * .2f;
		if (/* !isJumping() && */getY() + leeway > bounds.y + bounds.height) {
			collidingBounds = Pools.obtain(Rectangle.class).set(bounds);
			// test if we are above the bounds
			if (getX() < bounds.x + bounds.width || getRight() > bounds.x) {
				if (yVel < 0) {
					yVel = 0;
					if (getY() < bounds.y + bounds.height) {
						setY(bounds.y + bounds.height);
					}
				}
			}
		}
	}

	public boolean isStandingOn(Rectangle bounds) {
		// test if we are within a minimum y value to bounds top
		if (Math.abs(getY() - (bounds.y + bounds.height)) < .000001f) {
			// test if we are above the bounds
			if (getX() < bounds.x + bounds.width && getRight() > bounds.x) {
				return true;
			}
		}
		return false;
	}

	private boolean isOnGround() {
		return collidingBounds != null;
	}

	private void died() {
		GameEvent.fire("Avatar.onDeath", this);
		xVel = yVel = 0;
		dead = true;
	}
}
