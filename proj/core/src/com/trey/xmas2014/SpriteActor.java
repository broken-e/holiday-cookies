package com.trey.xmas2014;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * possibly could have just used Image with a SpriteDrawable...
 * 
 * @author trey miller
 */
public class SpriteActor extends Actor {

	protected final AtlasSprite sprite;
	public boolean spriteChanges = true;
	private AtlasRegion region;

	public SpriteActor(AtlasRegion region) {
		sprite = new AtlasSprite(region);
		this.region = region;
	}

	public void draw(Batch batch, float parentAlpha) {
		if (spriteChanges) {
			sprite.setOrigin(getOriginX(), getOriginY());
			sprite.setRotation(getRotation());
			sprite.setScale(getScaleX(), getScaleY());
			sprite.setColor(getColor());
			sprite.setBounds(getX(), getY(), getWidth(), getHeight());
		}
		sprite.draw(batch, parentAlpha);
	}

	public void flip(boolean x, boolean y) {
		sprite.flip(x, y);
	}

	public AtlasRegion getRegion() {
		return region;
	}
}
