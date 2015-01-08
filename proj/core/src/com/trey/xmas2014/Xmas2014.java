package com.trey.xmas2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.trey.xmas2014.MusicManager.MusicSample;

/**
 * main entry point, inheriting from UiApp
 * 
 * @author trey miller
 */
public class Xmas2014 extends UiApp {

	private Bg bg;
	private Snow snow;
	public ShapeRenderer shaper;
	public Player player;
	private MusicManager music;

	@Override
	public void create() {
		super.create();
		shaper = new ShapeRenderer();

		bg = new Bg(this);
		snow = new Snow(atlas, 21, 1f);

		stage.addActor(bg);
		stage.addActor(snow);

		bg.setZIndex(0);
		snow.setZIndex(42);

		player = new Player(this);

		atlas.findRegion("bg/moon").getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		atlas.findRegion("white").getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public void queueMusic(MusicSample sample, boolean removeQueued) {
		if (music == null) {
			music = new MusicManager(this);
			stage.addActor(music);
		}
		music.queueSample(sample, removeQueued);
	}

	public void fadeMusic() {
		if (music != null) {
			music.fadeOut();
		}
	}

	public void setWind(float wind) {
		snow.setWind(wind);
		bg.setWind(wind);
	}

	@Override
	public void switchScreens(BaseScreen screen) {
		super.switchScreens(screen);
		snow.setZIndex(42);
	}

	@Override
	protected String atlasPath() {
		return "skin.atlas";
	}

	@Override
	protected String skinPath() {
		return "uiskin.json";
	}

	@Override
	protected void styleSkin(Skin skin, TextureAtlas atlas) {
		String fontName = "dickens-56";
		String fontFile = fontName + ".fnt";
		// fonts
		BitmapFont smallFont = new BitmapFont(Gdx.files.internal(fontFile), skin.getRegion(fontName));
		BitmapFont mediumFont = new BitmapFont(Gdx.files.internal(fontFile), skin.getRegion(fontName));
		BitmapFont largeFont = new BitmapFont(Gdx.files.internal(fontFile), skin.getRegion(fontName));

		smallFont.setScale(.5f);
		mediumFont.setScale(.75f);

		skin.add("xmas-sm", smallFont);
		skin.add("xmas-md", mediumFont);
		skin.add("xmas-lg", largeFont);

		// label styles
		LabelStyle lsSm = new LabelStyle(smallFont, Color.WHITE);
		LabelStyle lsMd = new LabelStyle(mediumFont, Color.WHITE);
		LabelStyle lsLg = new LabelStyle(largeFont, Color.WHITE);

		skin.add("xmas-sm", lsSm);
		skin.add("xmas-md", lsMd);
		skin.add("xmas-lg", lsLg);

		// TextButton styles
		TextButtonStyle tbs = new TextButtonStyle();
		tbs.font = largeFont;
		skin.add("xmas-lg", tbs);

		// text area
		TextFieldStyle tas = skin.get(TextFieldStyle.class);
		tas.font = smallFont;
		tas.background = new TextureRegionDrawable(atlas.findRegion("white"));
		tas.fontColor = Color.RED;

	}

	@Override
	protected BaseScreen getFirstScreen() {
		return new MainScreen(this);
	}

	public void gameCameraChanged(float x, float y, float deltaX, float deltaY) {
		bg.moveX(deltaX);
	}

	public void forceMusicChange() {
		if (music != null) {
			music.forceNext();
		}
	}
}
