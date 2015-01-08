package com.trey.xmas2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Some "yagni" stuff I thought I would need, to loop and queue multiple music tracks.
 * 
 * @author trey miller
 */
public class MusicManager extends Actor {

	public enum MusicSample {
		GameOver("xmas-2014-ending.ogg"), Intro("xmas-2014-theme.ogg");
		public final String path;

		MusicSample(String path) {
			this.path = path;
		}
	}

	private Music current;
	private final Xmas2014 app;

	// linked list would be better if it mattered
	private final Array<MusicSample> queuedSamples = new Array<MusicSample>();

	private final float fadeDuration = 1.2f;
	private float fadeTime = 0;

	public MusicManager(Xmas2014 app) {
		this.app = app;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (queuedSamples.size > 0) {
			if (current != null) {
				current.setLooping(false);
				if (!current.isPlaying()) {
					current.dispose();
					current = null;
				}
			}
			if (current == null) {
				current = Gdx.audio.newMusic(Gdx.files.internal(queuedSamples.removeIndex(0).path));
				current.play();
			}
		} else {
			if (current != null) {
				current.setLooping(true);
			}
		}
		if (current != null) {
			if (current.isPlaying() && fadeTime > 0) {
				fadeTime -= delta;
				if (fadeTime <= 0) {
					current.stop();
					current.dispose();
					current = null;
					fadeTime = 0;
				} else {
					current.setVolume(MathUtils.clamp(fadeTime / fadeDuration, 0, 1));
				}
			} else if (!current.isPlaying()) {
				current.play();
			}
		}

	}

	/**
	 * queue up a MusicSample and optionally force all previously queued music to be cleared after current sample is done.
	 */
	public void queueSample(MusicSample sample, boolean removeQueued) {
		if (removeQueued) {
			queuedSamples.clear();
		}
		queuedSamples.add(sample);
	}

	public void fadeOut() {
		if (fadeTime > 0 && queuedSamples.size > 0) {
			queuedSamples.removeIndex(0);
		}
		fadeTime = fadeDuration;
	}

	public void forceNext() {
		if (queuedSamples.size > 0) {
			fadeTime = Float.MIN_NORMAL;
		}
	}
}
