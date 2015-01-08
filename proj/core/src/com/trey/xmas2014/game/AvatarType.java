package com.trey.xmas2014.game;

/** @author trey miller */
public enum AvatarType {
	Default("game/playerWalk", "game/playerStanding", "game/playerWalk");

	public final String walkRegions, standRegions, jumpRegions;

	AvatarType(String walkRegions, String standRegions, String jumpRegions) {
		this.walkRegions = walkRegions;
		this.standRegions = standRegions;
		this.jumpRegions = jumpRegions;
	}
}
