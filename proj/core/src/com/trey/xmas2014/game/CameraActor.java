package com.trey.xmas2014.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * sets the camera to this and updates in act() so that actions will be applied to the camera. getX() and getY() correspond to
 * cam.position, so they are not bottom left. Only camera fields position.x, position.y, viewportWidth, and viewportHeight are
 * affected.
 * 
 * @author trey miller
 */
public class CameraActor extends Actor {
	private Camera cam;
	private boolean updateX = true, updateY = true, updateWidth = true, updateHeight = true;

	public CameraActor(Camera cam) {
		setCamera(cam);
	}

	public void setCamera(Camera cam) {
		this.cam = cam;
		updateThisByCamera();
	}

	public Camera getCamera() {
		return cam;
	}

	/** whether this actor should update the corresponding camera values. Todo: change to bit enum if more fn is needed. */
	public void setUpdateMode(boolean x, boolean y, boolean w, boolean h) {
		this.updateX = x;
		this.updateY = y;
		this.updateWidth = w;
		this.updateHeight = h;
	}

	@Override
	public void act(float delta) {
		updateThisByCamera();
		super.act(delta);
		updateCameraByThis();
	}

	private void updateThisByCamera() {
		setBounds(cam.position.x, cam.position.y, cam.viewportWidth, cam.viewportHeight);
	}

	private void updateCameraByThis() {
		if (updateX) {
			cam.position.x = getX();
		}
		if (updateY) {
			cam.position.y = getY();
		}
		if (updateWidth) {
			cam.viewportWidth = getWidth();
		}
		if (updateHeight) {
			cam.viewportHeight = getHeight();
		}
		cam.update();
	}
}
